package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.shared.messaging.MessagePublisher;
import com.mecanicadm.workorder_api.core.stockmovements.domain.StockMovements;
import com.mecanicadm.workorder_api.core.stockmovements.domain.port.StockMovementsGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderMaterialItem;
import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;
import com.mecanicadm.workorder_api.core.workorder.domain.events.WorkOrderCanceledEvent;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.CancelWorkOrderCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.mecanicadm.workorder_api.core.workorder.usecase.SagaCommandUseCaseTestSupport.givenStored;
import static com.mecanicadm.workorder_api.core.workorder.usecase.SagaCommandUseCaseTestSupport.workOrderIn;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CancelWorkOrderUseCaseTest {

    @Mock
    private WorkOrderGateway gateway;
    @Mock
    private StockMovementsGateway stockMovementsGateway;
    @Mock
    private MessagePublisher publisher;

    private CancelWorkOrderUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CancelWorkOrderUseCase(gateway, stockMovementsGateway, publisher);
    }

    @ParameterizedTest
    @ValueSource(strings = {"RECEIVED", "DIAGNOSED", "AWAITING_EXECUTION"})
    @DisplayName("Deve cancelar e publicar WorkOrderCanceledEvent com o motivo em qualquer estado anterior a execucao")
    void shouldCancelAndPublishEventWhenBeforeExecution(String step) {
        WorkOrder workOrder = workOrderIn(step);
        givenStored(gateway, workOrder);

        useCase.execute(new CancelWorkOrderCommand(workOrder.getId()));

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(publisher).publish(ArgumentMatchers.any(), captor.capture());
        Object message = captor.getValue();
        WorkOrderCanceledEvent event = assertInstanceOf(WorkOrderCanceledEvent.class, message);
        assertAll(
                () -> assertEquals(WorkOrderStatus.CANCELLED, workOrder.getStatus()),
                () -> assertEquals(workOrder.getId(), event.workOrderId())
        );
        verify(gateway).update(workOrder);
    }

    @Test
    @DisplayName("Deve ser idempotente: OS ja cancelada nao gera novo evento")
    void shouldDoNothingWhenAlreadyCancelled() {
        WorkOrder workOrder = workOrderIn("RECEIVED");
        workOrder.cancel();
        givenStored(gateway, workOrder);

        useCase.execute(new CancelWorkOrderCommand(workOrder.getId()));

        verify(publisher, never()).publish(ArgumentMatchers.any(), ArgumentMatchers.any());
        verify(gateway, never()).update(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"IN_EXECUTION", "EXECUTION_COMPLETED", "PAID", "DELIVERED"})
    @DisplayName("Deve cancelar a partir da execucao em diante, publicando o evento e persistindo")
    void shouldCancelWhenExecutionAlreadyStarted(String step) {
        WorkOrder workOrder = workOrderIn(step);
        givenStored(gateway, workOrder);

        useCase.execute(new CancelWorkOrderCommand(workOrder.getId()));

        assertEquals(WorkOrderStatus.CANCELLED, workOrder.getStatus());
        verify(publisher).publish(ArgumentMatchers.eq(workOrder.getId()), ArgumentMatchers.any(WorkOrderCanceledEvent.class));
        verify(gateway).update(workOrder);
    }

    @Test
    @DisplayName("Deve lancar excecao quando a ordem de servico nao for encontrada")
    void shouldThrowWhenWorkOrderNotFound() {
        UUID id = UUID.randomUUID();
        when(gateway.findById(id)).thenReturn(Optional.empty());

        assertThrows(WorkOrderExceptions.NotFound.class,
                () -> useCase.execute(new CancelWorkOrderCommand(id)));
    }

    @Test
    @DisplayName("Deve liberar o estoque reservado dos materiais da OS ao cancelar")
    void shouldReleaseReservedStockWhenCancelling() {
        UUID materialId = UUID.randomUUID();
        WorkOrder created = workOrderIn("RECEIVED");
        WorkOrder workOrder = WorkOrder.restore(created.getId(), created.getClientId(), created.getVehicleId(), created.getDescription(),
                created.getStatus(), null, null, Set.of(), Set.of(WorkOrderMaterialItem.create(materialId, 2)), null, null, null);
        StockMovements stock = mock(StockMovements.class);
        givenStored(gateway, workOrder);
        when(stockMovementsGateway.findAllByMaterialIdAndWorkOrderId(materialId, workOrder.getId())).thenReturn(List.of(stock));

        useCase.execute(new CancelWorkOrderCommand(workOrder.getId()));

        verify(stock).delete();
        verify(stockMovementsGateway).update(stock);
        verify(gateway).update(workOrder);
    }

    @Test
    @DisplayName("Nao deve liberar estoque quando a OS ja estava cancelada")
    void shouldNotReleaseStockWhenAlreadyCancelled() {
        WorkOrder workOrder = workOrderIn("RECEIVED");
        workOrder.cancel();
        givenStored(gateway, workOrder);

        useCase.execute(new CancelWorkOrderCommand(workOrder.getId()));

        verifyNoInteractions(stockMovementsGateway);
    }
}
