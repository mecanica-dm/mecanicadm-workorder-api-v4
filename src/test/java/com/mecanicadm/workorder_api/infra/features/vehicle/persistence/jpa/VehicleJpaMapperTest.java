package com.mecanicadm.workorder_api.infra.features.vehicle.persistence.jpa;

import com.mecanicadm.workorder_api.core.vehicle.domain.Vehicle;
import com.mecanicadm.workorder_api.infra.features.vehicle.persistence.entity.VehicleJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class VehicleJpaMapperTest {

    @Test
    @DisplayName("Deve mapear JpaEntity para domínio")
    void shouldMapToDomain() {
        var now = LocalDateTime.now();
        var entity = new VehicleJpaEntity("ABC1234", "Civic", "Honda", (short) 2023);
        entity.setDateCreated(now);
        entity.setDateUpdated(now);
        entity.setDeletedAt(null);

        var domain = VehicleJpaMapper.toDomain(entity);

        assertEquals("ABC1234", domain.getLicensePlate());
        assertEquals("Civic", domain.getModel());
        assertEquals("Honda", domain.getBrand());
        assertEquals(Short.valueOf((short) 2023), domain.getModelYear());
        assertEquals(now, domain.getDateCreated());
        assertEquals(now, domain.getDateUpdated());
    }

    @Test
    @DisplayName("Deve mapear domínio para JpaEntity")
    void shouldMapToEntity() {
        var vehicle = Vehicle.create("Civic", "ABC1234", "Honda", (short) 2023);

        var entity = VehicleJpaMapper.toEntity(vehicle);

        assertEquals("ABC1234", entity.getLicensePlate());
        assertEquals("Civic", entity.getModel());
        assertEquals("Honda", entity.getBrand());
        assertEquals(Short.valueOf((short) 2023), entity.getModelYear());
        assertNotNull(entity.getDateCreated());
        assertNotNull(entity.getDateUpdated());
    }
}
