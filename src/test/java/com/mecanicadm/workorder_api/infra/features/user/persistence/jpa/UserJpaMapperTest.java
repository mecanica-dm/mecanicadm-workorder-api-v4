package com.mecanicadm.workorder_api.infra.features.user.persistence.jpa;

import com.mecanicadm.workorder_api.core.user.domain.User;
import com.mecanicadm.workorder_api.core.user.domain.enums.UserRole;
import com.mecanicadm.workorder_api.infra.features.user.persistence.entity.UserJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserJpaMapperTest {

    @Test
    @DisplayName("Deve mapear domínio para JpaEntity")
    void shouldMapToEntity() {
        var user = User.create("test@test.com", "encodedPassword", "Test User");

        var entity = UserJpaMapper.toEntity(user);

        assertEquals(user.getId(), entity.getId());
        assertEquals("test@test.com", entity.getEmail());
        assertEquals("encodedPassword", entity.getPassword());
        assertEquals("Test User", entity.getName());
        assertEquals(user.getRoles(), entity.getRoles());
        assertNotNull(entity.getDateCreated());
        assertNotNull(entity.getDateUpdated());
    }

    @Test
    @DisplayName("Deve atualizar JpaEntity a partir do domínio")
    void shouldUpdateEntity() {
        var entity = new UserJpaEntity();
        entity.setId(UUID.randomUUID());
        var user = User.create("new@test.com", "newPassword", "New Name");

        UserJpaMapper.updateEntity(entity, user);

        assertEquals("new@test.com", entity.getEmail());
        assertEquals("newPassword", entity.getPassword());
        assertEquals("New Name", entity.getName());
        assertEquals(user.getRoles(), entity.getRoles());
        assertNotNull(entity.getDateUpdated());
    }

    @Test
    @DisplayName("Deve mapear JpaEntity para domínio")
    void shouldMapToDomain() {
        var id = UUID.randomUUID();
        var now = LocalDateTime.now();
        var entity = new UserJpaEntity();
        entity.setId(id);
        entity.setEmail("test@test.com");
        entity.setPassword("encoded");
        entity.setName("Test");
        entity.setRoles(List.of(UserRole.USER));
        entity.setDateCreated(now);
        entity.setDateUpdated(now);
        entity.setDeletedAt(null);

        var domain = UserJpaMapper.toDomain(entity);

        assertEquals(id, domain.getId());
        assertEquals("test@test.com", domain.getEmail());
        assertEquals("encoded", domain.getPassword());
        assertEquals("Test", domain.getName());
        assertEquals(List.of(UserRole.USER), domain.getRoles());
        assertEquals(now, domain.getDateCreated());
        assertEquals(now, domain.getDateUpdated());
    }
}
