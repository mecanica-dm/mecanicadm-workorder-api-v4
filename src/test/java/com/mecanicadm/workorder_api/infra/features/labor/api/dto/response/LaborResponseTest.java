package com.mecanicadm.workorder_api.infra.features.labor.api.dto.response;

import com.mecanicadm.workorder_api.core.labor.domain.Labor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LaborResponseTest {

    @Test
    @DisplayName("Deve criar LaborResponse a partir de um Labor via factory method")
    void shouldCreateLaborResponseFromLabor() {
        var id = UUID.randomUUID();
        var labor = Labor.restore(id, "Troca de Óleo", new BigDecimal("50.00"), null, null, null);

        LaborResponse response = LaborResponse.from(labor);

        assertNotNull(response);
        assertEquals(id, response.id());
        assertEquals("Troca de Óleo", response.name());
        assertEquals(new BigDecimal("50.00"), response.price());
    }
}
