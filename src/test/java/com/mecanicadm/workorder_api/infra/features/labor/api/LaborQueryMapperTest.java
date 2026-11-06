package com.mecanicadm.workorder_api.infra.features.labor.api;

import com.mecanicadm.workorder_api.core.labor.domain.Labor;
import com.mecanicadm.workorder_api.core.labor.domain.port.LaborPageResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LaborQueryMapperTest {

    @Test
    @DisplayName("Deve converter parâmetros de consulta para SearchLaborsQuery")
    void shouldConvertToQuery() {
        var pageable = PageRequest.of(0, 20, Sort.by("name").ascending());

        var query = LaborQueryMapper.toQuery("Óleo", pageable);

        assertEquals("Óleo", query.name());
        assertEquals(0, query.page());
        assertEquals(20, query.size());
        assertEquals("name", query.sortBy());
        assertEquals("ASC", query.direction());
    }

    @Test
    @DisplayName("Deve usar valores padrão para sort quando não especificado")
    void shouldUseDefaultSortWhenNotProvided() {
        var pageable = PageRequest.of(1, 10);

        var query = LaborQueryMapper.toQuery(null, pageable);

        assertEquals(1, query.page());
        assertEquals(10, query.size());
        assertEquals("name", query.sortBy());
        assertEquals("ASC", query.direction());
    }

    @Test
    @DisplayName("Deve converter LaborPageResult para Page de LaborResponse")
    void shouldConvertToPage() {
        var labor = Labor.create("Troca de Óleo", new BigDecimal("150.00"));
        var result = new LaborPageResult(List.of(labor), 1L);
        var pageable = PageRequest.of(0, 20);

        var page = LaborQueryMapper.toPage(result, pageable);

        assertEquals(1, page.getContent().size());
        assertEquals(1, page.getTotalElements());
        assertEquals("Troca de Óleo", page.getContent().getFirst().name());
    }
}
