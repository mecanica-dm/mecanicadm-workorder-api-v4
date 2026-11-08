package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.query.GetWorkOrderStatusQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetWorkOrderStatusUseCaseTest {

    @Mock
    private WorkOrderGateway gateway;

    @InjectMocks
    private GetWorkOrderStatusUseCase useCase;

    @Test
    @DisplayName("Deve retornar o status quando a ordem de serviço for encontrada")
    void shouldReturnStatusWhenWorkOrderFound() {
        UUID id = UUID.randomUUID();
        when(gateway.findStatusById(id)).thenReturn(Optional.of(WorkOrderStatus.RECEIVED));

        WorkOrderStatus result = useCase.execute(new GetWorkOrderStatusQuery(id));

        assertEquals(WorkOrderStatus.RECEIVED, result);
        verify(gateway).findStatusById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção quando a ordem de serviço não for encontrada")
    void shouldThrowExceptionWhenWorkOrderNotFound() {
        UUID id = UUID.randomUUID();
        when(gateway.findStatusById(id)).thenReturn(Optional.empty());

        assertThrows(WorkOrderExceptions.NotFound.class,
                () -> useCase.execute(new GetWorkOrderStatusQuery(id)));

        verify(gateway).findStatusById(id);
    }
}
