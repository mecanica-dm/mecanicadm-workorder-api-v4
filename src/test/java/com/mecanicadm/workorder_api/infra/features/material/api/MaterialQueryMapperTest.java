package com.mecanicadm.workorder_api.infra.features.material.api;

import com.mecanicadm.workorder_api.core.material.domain.Material;
import com.mecanicadm.workorder_api.core.material.domain.enums.MaterialType;
import com.mecanicadm.workorder_api.core.material.domain.port.MaterialPageResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MaterialQueryMapperTest {

    @Test
    @DisplayName("Deve converter parâmetros de consulta para SearchMaterialsQuery")
    void shouldConvertToQuery() {
        var pageable = PageRequest.of(0, 20, Sort.by("name").ascending());

        var query = MaterialQueryMapper.toQuery("Óleo", "Castrol", MaterialType.CONSUMABLE, pageable);

        assertEquals("Óleo", query.name());
        assertEquals("Castrol", query.brand());
        assertEquals(MaterialType.CONSUMABLE, query.type());
        assertEquals(0, query.page());
        assertEquals(20, query.size());
        assertEquals("name", query.sortBy());
        assertEquals("ASC", query.direction());
    }

    @Test
    @DisplayName("Deve usar valores padrão para sort quando não especificado")
    void shouldUseDefaultSortWhenNotProvided() {
        var pageable = PageRequest.of(1, 10);

        var query = MaterialQueryMapper.toQuery(null, null, null, pageable);

        assertEquals(1, query.page());
        assertEquals(10, query.size());
        assertEquals("name", query.sortBy());
        assertEquals("ASC", query.direction());
    }

    @Test
    @DisplayName("Deve converter MaterialPageResult para Page de MaterialResponse")
    void shouldConvertToPage() {
        var material = Material.create("Óleo", "Castrol", "Desc", new BigDecimal("65.00"), MaterialType.CONSUMABLE);
        var result = new MaterialPageResult(List.of(material), 1L);
        var pageable = PageRequest.of(0, 20);

        var page = MaterialQueryMapper.toPage(result, pageable);

        assertEquals(1, page.getContent().size());
        assertEquals(1, page.getTotalElements());
        assertEquals("Óleo", page.getContent().getFirst().name());
    }
}
