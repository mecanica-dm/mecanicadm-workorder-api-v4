package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.labor.domain.port.LaborGateway;
import com.mecanicadm.workorder_api.core.labor.exception.LaborExceptions;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderLaborItem;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.CreateWorkOrderLaborItemCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateWorkOrderLaborItemUseCaseTest {

    @Mock
    private LaborGateway laborGateway;

    @InjectMocks
    private CreateWorkOrderLaborItemUseCase useCase;

    @Test
    @DisplayName("Deve criar um item de serviço na ordem de serviço com sucesso")
    void shouldCreateWorkOrderLaborItemSuccessfully() {
        UUID laborId = UUID.randomUUID();
        when(laborGateway.existsById(laborId)).thenReturn(true);

        WorkOrderLaborItem result = useCase.execute(new CreateWorkOrderLaborItemCommand(laborId, UUID.randomUUID()));

        assertNotNull(result);
        verify(laborGateway).existsById(laborId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o serviço não for encontrado")
    void shouldThrowExceptionWhenLaborNotFound() {
        UUID laborId = UUID.randomUUID();
        when(laborGateway.existsById(laborId)).thenReturn(false);

        assertThrows(LaborExceptions.LaborNotFound.class,
                () -> useCase.execute(new CreateWorkOrderLaborItemCommand(laborId, UUID.randomUUID())));

        verify(laborGateway).existsById(laborId);
    }
}
