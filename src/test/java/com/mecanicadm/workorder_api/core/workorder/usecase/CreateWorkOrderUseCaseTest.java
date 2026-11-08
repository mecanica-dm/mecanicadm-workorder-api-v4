package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.shared.messaging.MessagePublisher;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.events.ClientSnapshot;
import com.mecanicadm.workorder_api.core.workorder.domain.events.VehicleSnapshot;
import com.mecanicadm.workorder_api.core.workorder.domain.events.WorkOrderCreatedEvent;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderDetails;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.CreateWorkOrderCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.CreateWorkOrderMaterialItemCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateWorkOrderUseCaseTest {

    @Mock
    private WorkOrderGateway gateway;
    @Mock
    private AddLaborToWorkOrderUseCase addLaborToWorkOrderUseCase;
    @Mock
    private AddMaterialToWorkOrderUseCase addMaterialToWorkOrderUseCase;
    @Mock
    private MessagePublisher publisher;

    private CreateWorkOrderUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateWorkOrderUseCase(gateway, addLaborToWorkOrderUseCase, addMaterialToWorkOrderUseCase, publisher);

        AtomicReference<WorkOrder> stored = new AtomicReference<>();
        when(gateway.create(any())).thenAnswer(invocation -> {
            stored.set(invocation.getArgument(0));
            return stored.get();
        });
        when(gateway.findDetailsById(any())).thenAnswer(invocation -> {
            WorkOrder created = stored.get();
            return Optional.of(new WorkOrderDetails(
                    created.getId(),
                    new ClientSnapshot(created.getClientId(), "Cliente", "cliente@test.com", "12345678900", "48999999999"),
                    new VehicleSnapshot(created.getVehicleId(), "Modelo", "Marca", 2020),
                    created.getDescription(),
                    created.getStatus(), null, null, List.of(), List.of()));
        });
    }

    private Object publishedMessage() {
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(publisher).publish(ArgumentMatchers.any(), captor.capture());
        return captor.getValue();
    }

    @Test
    @DisplayName("Deve criar uma ordem de serviço e publicar um unico WorkOrderCreatedEvent com o retrato da OS")
    void shouldCreateWorkOrderAndPublishSingleCreatedEvent() {
        UUID clientId = UUID.randomUUID();
        CreateWorkOrderCommand command = new CreateWorkOrderCommand(clientId, "ABC-1234", "Troca de oleo", null, null);

        UUID resultId = useCase.execute(command);

        Object message = publishedMessage();
        WorkOrderCreatedEvent event = assertInstanceOf(WorkOrderCreatedEvent.class, message);
        assertAll(
                () -> assertNotNull(resultId),
                () -> assertEquals(resultId, event.workOrderId()),
                () -> assertEquals(clientId, event.client().id()),
                () -> assertEquals("ABC-1234", event.vehicle().licensePlate()),
                () -> assertEquals("RECEIVED", event.status())
        );
        verify(gateway).create(any());
        verify(gateway, never()).update(any());
        verifyNoInteractions(addLaborToWorkOrderUseCase, addMaterialToWorkOrderUseCase);
    }

    @Test
    @DisplayName("Deve vincular servicos sem emitir WorkOrderUpdatedEvent")
    void shouldAttachLaborItemsWithoutEmittingUpdatedEvent() {
        UUID laborId1 = UUID.randomUUID();
        UUID laborId2 = UUID.randomUUID();
        CreateWorkOrderCommand command = new CreateWorkOrderCommand(
                UUID.randomUUID(), "ABC-1234", "Troca de oleo", List.of(laborId1, laborId2), null);

        UUID resultId = useCase.execute(command);

        verify(addLaborToWorkOrderUseCase).attach(resultId, laborId1);
        verify(addLaborToWorkOrderUseCase).attach(resultId, laborId2);
        verifyNoInteractions(addMaterialToWorkOrderUseCase);
        assertInstanceOf(WorkOrderCreatedEvent.class, publishedMessage());
    }

    @Test
    @DisplayName("Deve vincular materiais sem emitir WorkOrderUpdatedEvent")
    void shouldAttachMaterialItemsWithoutEmittingUpdatedEvent() {
        UUID materialId1 = UUID.randomUUID();
        UUID materialId2 = UUID.randomUUID();
        var materialItems = List.of(
                new CreateWorkOrderMaterialItemCommand(null, materialId1, 2),
                new CreateWorkOrderMaterialItemCommand(null, materialId2, 1));
        CreateWorkOrderCommand command = new CreateWorkOrderCommand(
                UUID.randomUUID(), "ABC-1234", "Troca de oleo", null, materialItems);

        UUID resultId = useCase.execute(command);

        verify(addMaterialToWorkOrderUseCase).attach(resultId, materialId1, 2);
        verify(addMaterialToWorkOrderUseCase).attach(resultId, materialId2, 1);
        verifyNoInteractions(addLaborToWorkOrderUseCase);
        assertInstanceOf(WorkOrderCreatedEvent.class, publishedMessage());
    }

    @Test
    @DisplayName("Deve vincular servicos e materiais e publicar apenas o evento de criacao")
    void shouldAttachLaborAndMaterialAndPublishOnlyCreatedEvent() {
        UUID laborId = UUID.randomUUID();
        UUID materialId = UUID.randomUUID();
        CreateWorkOrderCommand command = new CreateWorkOrderCommand(
                UUID.randomUUID(), "ABC-1234", "Troca de oleo", List.of(laborId),
                List.of(new CreateWorkOrderMaterialItemCommand(null, materialId, 3)));

        UUID resultId = useCase.execute(command);

        verify(addLaborToWorkOrderUseCase, times(1)).attach(resultId, laborId);
        verify(addMaterialToWorkOrderUseCase, times(1)).attach(resultId, materialId, 3);
        verify(publisher, times(1)).publish(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Deve criar OS com listas vazias sem vincular itens")
    void shouldCreateWorkOrderWithEmptyListsWithoutAttachingItems() {
        CreateWorkOrderCommand command = new CreateWorkOrderCommand(
                UUID.randomUUID(), "ABC-1234", "Troca de oleo", List.of(), List.of());

        useCase.execute(command);

        verifyNoInteractions(addLaborToWorkOrderUseCase, addMaterialToWorkOrderUseCase);
        verify(publisher, times(1)).publish(ArgumentMatchers.any(), ArgumentMatchers.any());
    }
}
