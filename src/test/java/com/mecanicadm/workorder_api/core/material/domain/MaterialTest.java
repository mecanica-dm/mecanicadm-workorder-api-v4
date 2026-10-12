package com.mecanicadm.workorder_api.core.material.domain;

import com.mecanicadm.workorder_api.core.material.domain.enums.MaterialType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MaterialTest {

    @Test
    @DisplayName("Deve criar um material com sucesso")
    void shouldCreateMaterialSuccessfully() {
        Material material = Material.create(
                "Óleo de Motor",
                "Castrol",
                "Óleo sintético 5W-30",
                new BigDecimal("80.00"),
                MaterialType.CONSUMABLE
        );

        assertNotNull(material);
        assertEquals("Óleo de Motor", material.getName());
        assertEquals("Castrol", material.getBrand());
        assertEquals("Óleo sintético 5W-30", material.getDescription());
        assertEquals(new BigDecimal("80.00"), material.getPrice());
        assertEquals(MaterialType.CONSUMABLE, material.getType());
    }

    @Test
    @DisplayName("Deve atualizar as informações do material com sucesso")
    void shouldUpdateMaterialSuccessfully() {
        Material material = Material.create(
                "Pneu Velho",
                "Marca Velha",
                "Descrição Velha",
                new BigDecimal("100.00"),
                MaterialType.PART
        );

        material.update(
                "Pneu Novo",
                "Marca Nova",
                "Descrição Nova",
                new BigDecimal("200.00"),
                MaterialType.CONSUMABLE
        );

        assertEquals("Pneu Novo", material.getName());
        assertEquals("Marca Nova", material.getBrand());
        assertEquals("Descrição Nova", material.getDescription());
        assertEquals(new BigDecimal("200.00"), material.getPrice());
        assertEquals(MaterialType.CONSUMABLE, material.getType());
    }
}
