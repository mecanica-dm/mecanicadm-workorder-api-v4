package com.mecanicadm.workorder_api.core.labor.usecase;

import com.mecanicadm.workorder_api.core.labor.domain.Labor;
import com.mecanicadm.workorder_api.core.labor.domain.port.LaborGateway;
import com.mecanicadm.workorder_api.core.labor.exception.LaborExceptions;
import com.mecanicadm.workorder_api.core.labor.usecase.command.UpdateLaborCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateLaborUseCaseTest {

    private LaborGateway gateway;

    private UpdateLaborUseCase useCase;

    private UUID laborId;
    private UpdateLaborCommand command;
    private Labor existingLabor;

    @BeforeEach
    void setUp() {
        gateway = mock(LaborGateway.class);
        useCase = new UpdateLaborUseCase(gateway);
        laborId = UUID.randomUUID();
        command = new UpdateLaborCommand(
                laborId,
                "Alinhamento e Balanceamento",
                new BigDecimal("120.00")
        );
        existingLabor = Labor.create(
                "Troca de Óleo",
                new BigDecimal("50.00")
        );
    }

    @Test
    @DisplayName("Deve atualizar um serviço existente com sucesso")
    void shouldUpdateExistingLaborSuccessfully() {
        when(gateway.findById(laborId)).thenReturn(Optional.of(existingLabor));
        when(gateway.update(any(Labor.class))).thenReturn(existingLabor);

        useCase.execute(command);

        verify(gateway).findById(laborId);
        verify(gateway).update(existingLabor);

        assertEquals(command.name(), existingLabor.getName());
        assertEquals(command.price(), existingLabor.getPrice());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o serviço não for encontrado para atualização")
    void shouldThrowExceptionWhenLaborNotFound() {
        when(gateway.findById(laborId)).thenReturn(Optional.empty());

        assertThrows(LaborExceptions.LaborNotFound.class, () -> useCase.execute(command));

        verify(gateway).findById(laborId);
        verify(gateway, never()).update(any(Labor.class));
    }
}
