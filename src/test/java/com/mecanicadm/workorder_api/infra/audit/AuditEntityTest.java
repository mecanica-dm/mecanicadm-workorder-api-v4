package com.mecanicadm.workorder_api.infra.audit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AuditEntityTest {

    @Test
    @DisplayName("Deve definir e obter dateCreated")
    void shouldSetAndGetDateCreated() {
        var entity = new TestAuditEntity();
        var now = LocalDateTime.now();

        entity.setDateCreated(now);

        assertEquals(now, entity.getDateCreated());
    }

    @Test
    @DisplayName("Deve definir e obter dateUpdated")
    void shouldSetAndGetDateUpdated() {
        var entity = new TestAuditEntity();
        var now = LocalDateTime.now();

        entity.setDateUpdated(now);

        assertEquals(now, entity.getDateUpdated());
    }

    @Test
    @DisplayName("Deve definir e obter deletedAt")
    void shouldSetAndGetDeletedAt() {
        var entity = new TestAuditEntity();
        var now = LocalDateTime.now();

        entity.setDeletedAt(now);

        assertEquals(now, entity.getDeletedAt());
    }

    @Test
    @DisplayName("Deve inicializar com todas as datas nulas")
    void shouldStartWithNullDates() {
        var entity = new TestAuditEntity();

        assertNull(entity.getDateCreated());
        assertNull(entity.getDateUpdated());
        assertNull(entity.getDeletedAt());
    }

    private static class TestAuditEntity extends AuditEntity {
    }
}
