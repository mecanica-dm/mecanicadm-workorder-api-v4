package com.mecanicadm.workorder_api.core.workorder.domain;

import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.shared.exception.DomainExceptionCore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderMaterialItemTest {

    @Test
    @DisplayName("Deve criar item de material com dados informados")
    void shouldCreateMaterialItemSuccessfully() {
        UUID materialId = UUID.randomUUID();
        WorkOrderMaterialItem item = WorkOrderMaterialItem.create(materialId, 2);

        assertEquals(materialId, item.getMaterialId());
        assertEquals(2, item.getQuantity());
    }

    @Test
    @DisplayName("Nao deve criar item de material com materialId nulo")
    void shouldNotCreateMaterialItemWithNullMaterialId() {
        int quantity = 1;

        assertThrows(
                NullPointerException.class,
                () -> WorkOrderMaterialItem.create(null, quantity)
        );
    }

    @Test
    @DisplayName("Nao deve criar item de material com quantidade zero")
    void shouldNotCreateMaterialItemWithZeroQuantity() {
        UUID id = UUID.randomUUID();

        DomainExceptionCore exception = assertThrows(
                DomainExceptionCore.class,
                () -> WorkOrderMaterialItem.create(id, 0)
        );

        assertAll(
                () -> assertInstanceOf(WorkOrderExceptions.InvalidMaterialQuantity.class, exception),
                () -> assertEquals("work.order.material.quantity.invalid", exception.getMessageKey()),
                () -> assertEquals(400, exception.getStatus())
        );
    }

    @Test
    @DisplayName("Nao deve criar item de material com quantidade negativa")
    void shouldNotCreateMaterialItemWithNegativeQuantity() {
        UUID id = UUID.randomUUID();

        DomainExceptionCore exception = assertThrows(
                DomainExceptionCore.class,
                () -> WorkOrderMaterialItem.create(id, -1)
        );

        assertAll(
                () -> assertInstanceOf(WorkOrderExceptions.InvalidMaterialQuantity.class, exception),
                () -> assertEquals("work.order.material.quantity.invalid", exception.getMessageKey()),
                () -> assertEquals(400, exception.getStatus())
        );
    }
}

