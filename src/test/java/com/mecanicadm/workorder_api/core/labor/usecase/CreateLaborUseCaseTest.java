package com.mecanicadm.workorder_api.core.labor.usecase;

import com.mecanicadm.workorder_api.core.labor.domain.Labor;
import com.mecanicadm.workorder_api.core.labor.domain.port.LaborGateway;
import com.mecanicadm.workorder_api.core.labor.usecase.command.CreateLaborCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateLaborUseCaseTest {

    private LaborGateway gateway;

    private CreateLaborUseCase useCase;

    private CreateLaborCommand command;

    @BeforeEach
    void setUp() {
        gateway = mock(LaborGateway.class);
        useCase = new CreateLaborUseCase(gateway);
        command = new CreateLaborCommand(
                "Troca de Filtro",
                new BigDecimal("40.00")
        );
    }

    @Test
    @DisplayName("Deve criar um serviço com sucesso")
    void shouldCreateLaborSuccessfully() {
        UUID expectedId = UUID.randomUUID();
        Labor savedLabor = mock(Labor.class);
        when(savedLabor.getId()).thenReturn(expectedId);
        when(gateway.create(any(Labor.class))).thenReturn(savedLabor);

        UUID resultId = useCase.execute(command);

        assertNotNull(resultId);
        assertEquals(expectedId, resultId);
        verify(gateway).create(any(Labor.class));
    }
}
