package com.mecanicadm.workorder_api.core.material.usecase;

import com.mecanicadm.workorder_api.core.material.domain.Material;
import com.mecanicadm.workorder_api.core.material.domain.enums.MaterialType;
import com.mecanicadm.workorder_api.core.material.domain.port.MaterialGateway;
import com.mecanicadm.workorder_api.core.material.domain.port.MaterialPageQuery;
import com.mecanicadm.workorder_api.core.material.domain.port.MaterialPageResult;
import com.mecanicadm.workorder_api.core.material.usecase.query.SearchMaterialsQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllMaterialsUseCaseTest {

    @Mock
    private MaterialGateway repository;

    @InjectMocks
    private GetAllMaterialsUseCase getAllMaterialsUseCase;

    @Test
    @DisplayName("Deve retornar uma página de materiais")
    void shouldReturnPageOfMaterials() {
        SearchMaterialsQuery query = new SearchMaterialsQuery(null, null, null, 0, 10, "name", "ASC");
        Material material = Material.create("Pneu", "Michelin", "Pneu de carro", new BigDecimal("500"), MaterialType.PART);
        MaterialPageResult expectedPage = new MaterialPageResult(List.of(material), 1);

        when(repository.findAll(argThat(q -> q.page() == 0 && q.size() == 10 && q.sortBy().equals("name") && q.direction().equals("ASC")))).thenReturn(expectedPage);

        MaterialPageResult result = getAllMaterialsUseCase.execute(query);

        assertNotNull(result);
        assertEquals(1, result.totalElements());
        assertEquals("Pneu", result.items().get(0).getName());
        verify(repository).findAll(any(MaterialPageQuery.class));
    }

    @Test
    @DisplayName("Deve retornar uma página vazia quando nenhum material for encontrado")
    void shouldReturnEmptyPageWhenNoMaterialFound() {
        SearchMaterialsQuery query = new SearchMaterialsQuery("Inexistente", null, null, 0, 10, "name", "ASC");
        MaterialPageResult expectedPage = new MaterialPageResult(List.of(), 0);

        when(repository.findAll(argThat(q -> q.page() == 0 && q.size() == 10 && q.sortBy().equals("name") && q.direction().equals("ASC")))).thenReturn(expectedPage);

        MaterialPageResult result = getAllMaterialsUseCase.execute(query);

        assertNotNull(result);
        assertTrue(result.items().isEmpty());
        verify(repository).findAll(any(MaterialPageQuery.class));
    }
}
