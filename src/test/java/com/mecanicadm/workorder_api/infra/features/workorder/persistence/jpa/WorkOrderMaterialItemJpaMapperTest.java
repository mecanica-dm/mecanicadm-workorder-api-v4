package com.mecanicadm.workorder_api.infra.features.workorder.persistence.jpa;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderMaterialItem;
import com.mecanicadm.workorder_api.infra.features.workorder.persistence.entity.WorkOrderMaterialItemJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderMaterialItemJpaMapperTest {

    @Test
    @DisplayName("Deve mapear JpaEntity para domínio")
    void shouldMapToDomain() {
        var id = UUID.randomUUID();
        var workOrderId = UUID.randomUUID();
        var materialId = UUID.randomUUID();
        var entity = new WorkOrderMaterialItemJpaEntity(id, workOrderId, materialId, 5);

        var domain = WorkOrderMaterialItemJpaMapper.toDomain(entity);

        assertEquals(id, domain.getId());
        assertEquals(materialId, domain.getMaterialId());
        assertEquals(5, domain.getQuantity());
    }

    @Test
    @DisplayName("Deve mapear domínio para JpaEntity")
    void shouldMapToEntity() {
        var id = UUID.randomUUID();
        var workOrderId = UUID.randomUUID();
        var materialId = UUID.randomUUID();
        var domain = WorkOrderMaterialItem.restore(id, materialId, 10);

        var entity = WorkOrderMaterialItemJpaMapper.toEntity(domain, workOrderId);

        assertEquals(id, entity.getId());
        assertEquals(workOrderId, entity.getWorkOrderId());
        assertEquals(materialId, entity.getMaterialId());
        assertEquals(10, entity.getQuantity());
    }

    @Test
    @DisplayName("Deve mapear conjunto de entidades para conjunto de domínios")
    void shouldMapToDomainSet() {
        var workOrderId = UUID.randomUUID();
        var entity1 = new WorkOrderMaterialItemJpaEntity(UUID.randomUUID(), workOrderId, UUID.randomUUID(), 1);
        var entity2 = new WorkOrderMaterialItemJpaEntity(UUID.randomUUID(), workOrderId, UUID.randomUUID(), 2);

        var domains = WorkOrderMaterialItemJpaMapper.toDomainSet(Set.of(entity1, entity2));

        assertNotNull(domains);
        assertEquals(2, domains.size());
    }

    @Test
    @DisplayName("Deve retornar null quando entidade for nula")
    void shouldReturnNullWhenEntityIsNull() {
        assertNull(WorkOrderMaterialItemJpaMapper.toDomain(null));
    }

    @Test
    @DisplayName("Deve retornar null quando domínio for nulo")
    void shouldReturnNullWhenDomainIsNull() {
        assertNull(WorkOrderMaterialItemJpaMapper.toEntity(null, UUID.randomUUID()));
    }

    @Test
    @DisplayName("Deve retornar vazio quando conjunto de entidades for nulo")
    void shouldReturnEmptyWhenEntitiesSetIsNull() {
        assertTrue(WorkOrderMaterialItemJpaMapper.toDomainSet(null).isEmpty());
    }
}
