package com.mecanicadm.workorder_api.infra.features.client.api;

import com.mecanicadm.workorder_api.core.client.domain.Client;
import com.mecanicadm.workorder_api.core.client.domain.port.ClientPageResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientQueryMapperTest {

    @Test
    @DisplayName("Deve converter parâmetros de consulta para GetAllClientQuery")
    void shouldConvertToQuery() {
        var pageable = PageRequest.of(0, 20, Sort.by("name").ascending());

        var query = ClientQueryMapper.toQuery("João", "12345678901", pageable);

        assertEquals("João", query.name());
        assertEquals("12345678901", query.document());
        assertEquals(0, query.page());
        assertEquals(20, query.size());
        assertEquals("name", query.sortBy());
        assertEquals("ASC", query.direction());
    }

    @Test
    @DisplayName("Deve usar valores padrão para sort quando não especificado")
    void shouldUseDefaultSortWhenNotProvided() {
        var pageable = PageRequest.of(1, 10);

        var query = ClientQueryMapper.toQuery(null, null, pageable);

        assertEquals(1, query.page());
        assertEquals(10, query.size());
        assertEquals("name", query.sortBy());
        assertEquals("ASC", query.direction());
    }

    @Test
    @DisplayName("Deve converter ClientPageResult para Page de ClientResponse")
    void shouldConvertToPage() {
        var client = Client.create("Test", "test@test.com", "12345678901", "48999999999");
        var result = new ClientPageResult(List.of(client), 1L);
        var pageable = PageRequest.of(0, 20);

        var page = ClientQueryMapper.toPage(result, pageable);

        assertEquals(1, page.getContent().size());
        assertEquals(1, page.getTotalElements());
        assertEquals("Test", page.getContent().getFirst().name());
    }
}
