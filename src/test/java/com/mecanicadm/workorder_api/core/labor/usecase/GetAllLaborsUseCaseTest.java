package com.mecanicadm.workorder_api.core.labor.usecase;

import com.mecanicadm.workorder_api.core.labor.domain.Labor;
import com.mecanicadm.workorder_api.core.labor.domain.port.LaborGateway;
import com.mecanicadm.workorder_api.core.labor.domain.port.LaborPageResult;
import com.mecanicadm.workorder_api.core.labor.usecase.query.SearchLaborsQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllLaborsUseCaseTest {

    private LaborGateway gateway;

    private GetAllLaborsUseCase useCase;

    @BeforeEach
    void setUp() {
        gateway = mock(LaborGateway.class);
        useCase = new GetAllLaborsUseCase(gateway);
    }

    @Test
    @DisplayName("Deve retornar uma página de serviço")
    void shouldReturnPageOfLabors() {
        SearchLaborsQuery query = new SearchLaborsQuery("Alinhamento", 0, 10, "name", "ASC");
        Labor labor = Labor.create("Alinhamento", new BigDecimal("100"));
        LaborPageResult pageResult = new LaborPageResult(List.of(labor), 1);

        when(gateway.findAll(any())).thenReturn(pageResult);

        LaborPageResult response = useCase.execute(query);

        assertNotNull(response);
        assertEquals(1, response.totalElements());
        assertEquals("Alinhamento", response.items().getFirst().getName());
        verify(gateway).findAll(any());
    }

    @Test
    @DisplayName("Deve retornar uma página vazia quando nenhum serviço for encontrado")
    void shouldReturnEmptyPageWhenNoLaborFound() {
        SearchLaborsQuery query = new SearchLaborsQuery("Inexistente", 0, 10, "name", "ASC");
        LaborPageResult pageResult = new LaborPageResult(List.of(), 0);

        when(gateway.findAll(any())).thenReturn(pageResult);

        LaborPageResult response = useCase.execute(query);

        assertNotNull(response);
        assertEquals(0, response.totalElements());
        verify(gateway).findAll(any());
    }
}
