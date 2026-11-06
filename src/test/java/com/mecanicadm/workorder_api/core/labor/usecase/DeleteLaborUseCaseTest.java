package com.mecanicadm.workorder_api.core.labor.usecase;

import com.mecanicadm.workorder_api.core.labor.domain.Labor;
import com.mecanicadm.workorder_api.core.labor.domain.port.LaborGateway;
import com.mecanicadm.workorder_api.core.labor.exception.LaborExceptions;
import com.mecanicadm.workorder_api.core.labor.usecase.command.DeleteLaborCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteLaborUseCaseTest {

    @Mock
    private LaborGateway gateway;

    @InjectMocks
    private DeleteLaborUseCase useCase;

    @Test
    @DisplayName("Deve deletar um serviço existente com sucesso")
    void shouldDeleteExistingLaborSuccessfully() {
        UUID laborId = UUID.randomUUID();
        Labor labor = Labor.create("Alinhamento", new BigDecimal("100"));
        when(gateway.findById(laborId)).thenReturn(Optional.of(labor));

        useCase.execute(new DeleteLaborCommand(laborId));

        verify(gateway).findById(laborId);
        verify(gateway).update(labor);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o serviço não for encontrado para exclusão")
    void shouldThrowExceptionWhenLaborNotFound() {
        UUID laborId = UUID.randomUUID();
        when(gateway.findById(laborId)).thenReturn(Optional.empty());

        assertThrows(LaborExceptions.LaborNotFound.class, () -> useCase.execute(new DeleteLaborCommand(laborId)));

        verify(gateway).findById(laborId);
        verify(gateway, never()).update(any());
    }
}
