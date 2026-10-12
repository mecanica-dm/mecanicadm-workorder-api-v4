package com.mecanicadm.workorder_api.core.labor.usecase;

import com.mecanicadm.workorder_api.core.labor.domain.Labor;
import com.mecanicadm.workorder_api.core.labor.domain.port.LaborGateway;
import com.mecanicadm.workorder_api.core.labor.exception.LaborExceptions;
import com.mecanicadm.workorder_api.core.labor.usecase.query.GetLaborByIdQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetLaborByIdUseCaseTest {

    private LaborGateway gateway;

    private GetLaborByIdUseCase useCase;

    @BeforeEach
    void setUp() {
        gateway = mock(LaborGateway.class);
        useCase = new GetLaborByIdUseCase(gateway);
    }

    @Test
    @DisplayName("Deve retornar um serviço quando o ID existir")
    void shouldReturnLaborWhenIdExists() {
        UUID id = UUID.randomUUID();
        Labor labor = Labor.restore(id, "Alinhamento", new BigDecimal("100"), null, null, null);

        when(gateway.findById(id)).thenReturn(Optional.of(labor));

        Labor response = useCase.execute(new GetLaborByIdQuery(id));

        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("Alinhamento", response.getName());
        verify(gateway).findById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o serviço não for encontrado")
    void shouldThrowExceptionWhenLaborNotFound() {
        UUID id = UUID.randomUUID();
        when(gateway.findById(id)).thenReturn(Optional.empty());

        assertThrows(LaborExceptions.LaborNotFound.class, () -> useCase.execute(new GetLaborByIdQuery(id)));
        verify(gateway).findById(id);
    }
}
