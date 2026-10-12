package com.mecanicadm.workorder_api.infra.features.workorder.persistence.jpa;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderBudget;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderLaborItem;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderMaterialItem;
import com.mecanicadm.workorder_api.core.workorder.domain.enums.LaborExecutionStatus;
import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderBudgetStatus;
import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;
import com.mecanicadm.workorder_api.infra.features.workorder.persistence.entity.WorkOrderJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderJpaMapperTest {

    @Test
    @DisplayName("Deve mapear JpaEntity para domínio com todos os relacionamentos")
    void shouldMapToDomainWithAllRelations() {
        var id = UUID.randomUUID();
        var clientId = UUID.randomUUID();
        var now = LocalDateTime.now();

        var laborItem = WorkOrderLaborItem.restore(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), now, now.plusHours(1), LaborExecutionStatus.EXECUTION_COMPLETED);
        var materialItem = WorkOrderMaterialItem.restore(UUID.randomUUID(), UUID.randomUUID(), 5);
        var budget = WorkOrderBudget.restore(id, new BigDecimal("500.00"), WorkOrderBudgetStatus.APPROVED, null);

        var entity = new WorkOrderJpaEntity(id, clientId, "ABC-1234", "Troca de óleo", WorkOrderStatus.IN_EXECUTION, now, null);
        entity.setDateCreated(now);
        entity.setDateUpdated(now);
        entity.setDeletedAt(null);

        var domain = WorkOrderJpaMapper.toDomain(entity, Set.of(laborItem), Set.of(materialItem), budget);

        assertEquals(id, domain.getId());
        assertEquals(clientId, domain.getClientId());
        assertEquals("ABC-1234", domain.getVehicleId());
        assertEquals("Troca de óleo", domain.getDescription());
        assertEquals(WorkOrderStatus.IN_EXECUTION, domain.getStatus());
        assertEquals(now, domain.getExecutionStartAt().orElse(null));
        assertTrue(domain.getExecutionEndAt().isEmpty());
        assertEquals(now, domain.getDateCreated());
        assertEquals(now, domain.getDateUpdated());
        assertNull(domain.getDeletedAt());
        assertEquals(1, domain.getLaborItems().size());
        assertEquals(1, domain.getMaterialItems().size());
        assertTrue(domain.getBudget().isPresent());
        assertEquals(new BigDecimal("500.00"), domain.getBudget().get().getTotalPrice());
    }

    @Test
    @DisplayName("Deve mapear domínio para JpaEntity sem os relacionamentos")
    void shouldMapToEntityWithoutRelations() {
        var id = UUID.randomUUID();
        var clientId = UUID.randomUUID();
        var now = LocalDateTime.now();

        var domain = WorkOrder.restore(id, clientId, "XYZ-9876", "Revisão completa", WorkOrderStatus.RECEIVED, null, null, Set.of(), Set.of(), null, now, now, null);

        var entity = WorkOrderJpaMapper.toEntity(domain);

        assertEquals(id, entity.getId());
        assertEquals(clientId, entity.getClientId());
        assertEquals("XYZ-9876", entity.getVehicleId());
        assertEquals("Revisão completa", entity.getDescription());
        assertEquals(WorkOrderStatus.RECEIVED, entity.getStatus());
        assertTrue(entity.getExecutionStartAt().isEmpty());
        assertTrue(entity.getExecutionEndAt().isEmpty());
        assertEquals(now, entity.getDateCreated());
        assertEquals(now, entity.getDateUpdated());
        assertNull(entity.getDeletedAt());
    }

    @Test
    @DisplayName("Deve retornar null quando entidade for nula no toDomain")
    void shouldReturnNullWhenEntityIsNullForToDomain() {
        assertNull(WorkOrderJpaMapper.toDomain(null, Set.of(), Set.of(), null));
    }

    @Test
    @DisplayName("Deve retornar null quando domínio for nulo no toEntity")
    void shouldReturnNullWhenDomainIsNullForToEntity() {
        assertNull(WorkOrderJpaMapper.toEntity(null));
    }

    @Test
    @DisplayName("Deve mapear JpaEntity para domínio sem relacionamentos no toDomainLight")
    void shouldMapToDomainLight() {
        var id = UUID.randomUUID();
        var clientId = UUID.randomUUID();
        var now = LocalDateTime.now();

        var entity = new WorkOrderJpaEntity(id, clientId, "ABC-1234", "Troca de óleo", WorkOrderStatus.RECEIVED, null, null);
        entity.setDateCreated(now);

        var domain = WorkOrderJpaMapper.toDomainLight(entity);

        assertEquals(id, domain.getId());
        assertEquals(clientId, domain.getClientId());
        assertTrue(domain.getLaborItems().isEmpty());
        assertTrue(domain.getMaterialItems().isEmpty());
        assertTrue(domain.getBudget().isEmpty());
    }

    @Test
    @DisplayName("Deve retornar null quando entidade for nula no toDomainLight")
    void shouldReturnNullWhenEntityIsNullForToDomainLight() {
        assertNull(WorkOrderJpaMapper.toDomainLight(null));
    }
}
