package com.mecanicadm.workorder_api.infra.features.workorder.persistence.jpa;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderBudget;
import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderBudgetStatus;
import com.mecanicadm.workorder_api.infra.features.workorder.persistence.entity.WorkOrderBudgetJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderBudgetJpaMapperTest {

    @Test
    @DisplayName("Deve mapear JpaEntity para domínio")
    void shouldMapToDomain() {
        var workOrderId = UUID.randomUUID();
        var entity = new WorkOrderBudgetJpaEntity(workOrderId, new BigDecimal("350.00"), WorkOrderBudgetStatus.APPROVED, null);

        var domain = WorkOrderBudgetJpaMapper.toDomain(entity);

        assertEquals(workOrderId, domain.getWorkOrderId());
        assertEquals(new BigDecimal("350.00"), domain.getTotalPrice());
        assertEquals(WorkOrderBudgetStatus.APPROVED, domain.getStatus());
        assertNull(domain.getObservation());
    }

    @Test
    @DisplayName("Deve mapear JpaEntity para domínio com observation")
    void shouldMapToDomainWithObservation() {
        var workOrderId = UUID.randomUUID();
        var entity = new WorkOrderBudgetJpaEntity(workOrderId, new BigDecimal("200.00"), WorkOrderBudgetStatus.REJECTED, "Preço muito alto");

        var domain = WorkOrderBudgetJpaMapper.toDomain(entity);

        assertEquals(workOrderId, domain.getWorkOrderId());
        assertEquals(new BigDecimal("200.00"), domain.getTotalPrice());
        assertEquals(WorkOrderBudgetStatus.REJECTED, domain.getStatus());
        assertEquals("Preço muito alto", domain.getObservation());
    }

    @Test
    @DisplayName("Deve mapear domínio para JpaEntity")
    void shouldMapToEntity() {
        var workOrderId = UUID.randomUUID();
        var domain = WorkOrderBudget.restore(workOrderId, new BigDecimal("400.00"), WorkOrderBudgetStatus.PENDING, null);

        var entity = WorkOrderBudgetJpaMapper.toEntity(domain);

        assertEquals(workOrderId, entity.getWorkOrderId());
        assertEquals(new BigDecimal("400.00"), entity.getTotalPrice());
        assertEquals(WorkOrderBudgetStatus.PENDING, entity.getStatus());
        assertNull(entity.getObservation());
    }

    @Test
    @DisplayName("Deve mapear domínio para JpaEntity com observation")
    void shouldMapToEntityWithObservation() {
        var workOrderId = UUID.randomUUID();
        var domain = WorkOrderBudget.restore(workOrderId, new BigDecimal("150.00"), WorkOrderBudgetStatus.CHANGES_REQUESTED, "Solicitar reajuste");

        var entity = WorkOrderBudgetJpaMapper.toEntity(domain);

        assertEquals(workOrderId, entity.getWorkOrderId());
        assertEquals(new BigDecimal("150.00"), entity.getTotalPrice());
        assertEquals(WorkOrderBudgetStatus.CHANGES_REQUESTED, entity.getStatus());
        assertEquals("Solicitar reajuste", entity.getObservation());
    }

    @Test
    @DisplayName("Deve retornar null quando entidade for nula")
    void shouldReturnNullWhenEntityIsNull() {
        assertNull(WorkOrderBudgetJpaMapper.toDomain(null));
    }

    @Test
    @DisplayName("Deve retornar null quando domínio for nulo")
    void shouldReturnNullWhenDomainIsNull() {
        assertNull(WorkOrderBudgetJpaMapper.toEntity(null));
    }
}
