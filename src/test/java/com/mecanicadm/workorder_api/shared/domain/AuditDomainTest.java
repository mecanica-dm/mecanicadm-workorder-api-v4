package com.mecanicadm.workorder_api.shared.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AuditDomainTest {

    private TestAuditDomain auditDomain;

    @BeforeEach
    void setUp() {
        auditDomain = new TestAuditDomain();
    }

    @Test
    @DisplayName("Deve inicializar com datas nulas")
    void shouldStartWithNullDates() {
        assertNull(auditDomain.getDateCreated());
        assertNull(auditDomain.getDateUpdated());
        assertNull(auditDomain.getDeletedAt());
        assertFalse(auditDomain.isDeleted());
    }

    @Test
    @DisplayName("Deve definir dateCreated e dateUpdated ao criar")
    void shouldSetDatesOnCreate() {
        auditDomain.create();

        assertNotNull(auditDomain.getDateCreated());
        assertNotNull(auditDomain.getDateUpdated());
        assertEquals(auditDomain.getDateCreated(), auditDomain.getDateUpdated());
    }

    @Test
    @DisplayName("Deve atualizar dateUpdated ao chamar update")
    void shouldUpdateDateUpdated() {
        auditDomain.create();
        LocalDateTime original = auditDomain.getDateUpdated();

        auditDomain.update();

        assertNotNull(auditDomain.getDateUpdated());
        assertTrue(auditDomain.getDateUpdated().isAfter(original)
                || auditDomain.getDateUpdated().equals(original));
    }

    @Test
    @DisplayName("Deve marcar como deletado ao chamar delete")
    void shouldMarkAsDeleted() {
        auditDomain.create();
        auditDomain.delete();

        assertNotNull(auditDomain.getDeletedAt());
        assertTrue(auditDomain.isDeleted());
    }

    @Test
    @DisplayName("Deve atualizar dateUpdated ao deletar")
    void shouldUpdateDateUpdatedOnDelete() {
        auditDomain.create();
        LocalDateTime created = auditDomain.getDateCreated();

        auditDomain.delete();

        assertNotNull(auditDomain.getDateUpdated());
        assertTrue(auditDomain.getDateUpdated().isAfter(created)
                || auditDomain.getDateUpdated().equals(created));
    }

    @Test
    @DisplayName("isDeleted deve retornar false quando deletedAt é nulo")
    void isDeletedShouldReturnFalseWhenDeletedAtIsNull() {
        assertFalse(auditDomain.isDeleted());
    }

    @Test
    @DisplayName("isDeleted deve retornar true quando deletedAt não é nulo")
    void isDeletedShouldReturnTrueWhenDeletedAtIsNotNull() {
        auditDomain.delete();
        assertTrue(auditDomain.isDeleted());
    }

    private static class TestAuditDomain extends AuditDomain {
    }
}
