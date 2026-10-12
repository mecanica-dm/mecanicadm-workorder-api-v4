package com.mecanicadm.workorder_api.core.user.domain;

import com.mecanicadm.workorder_api.core.user.domain.enums.UserRole;
import com.mecanicadm.workorder_api.core.user.exception.UserExceptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("Deve criar um usuário com sucesso")
    void shouldCreateUser() {
        User user = User.create("test@example.com", "encodedPassword", "Test Name");

        assertEquals("test@example.com", user.getEmail());
        assertEquals("encodedPassword", user.getPassword());
        assertEquals("Test Name", user.getName());
        assertTrue(user.getRoles().contains(UserRole.USER));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @DisplayName("Deve lançar exceção para senha nula, vazia ou apenas espaços")
    void shouldThrowExceptionForInvalidPasswordText(String invalidPassword) {
        assertThrows(UserExceptions.PasswordMinLength.class, () ->
                User.validatePassword(invalidPassword));
    }

    @Test
    @DisplayName("Deve lançar exceção para senha curta")
    void shouldThrowExceptionForShortPassword() {
        assertThrows(UserExceptions.PasswordMinLength.class, () ->
                User.validatePassword("12345"));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário com e-mail vazio")
    void shouldThrowExceptionForEmptyEmail() {
        assertThrows(UserExceptions.EmailNotEmpty.class, () ->
                User.create("", "encodedPassword", "Test Name"));
    }

    @Test
    @DisplayName("Deve atualizar informações do usuário")
    void shouldUpdateUserInfo() {
        User user = User.create("old@email.com", "encoded", "Old Name");

        user.updateInfo("New Name", "new@email.com");

        assertEquals("New Name", user.getName());
        assertEquals("new@email.com", user.getEmail());
    }

    @Test
    @DisplayName("Deve gerenciar roles do usuário")
    void shouldManageRoles() {
        User user = User.create("test@email.com", "encoded", "Name");

        user.addRole(UserRole.ATTENDANT);
        assertTrue(user.getRoles().contains(UserRole.ATTENDANT));

        user.removeRole(UserRole.ATTENDANT);
        assertFalse(user.getRoles().contains(UserRole.ATTENDANT));
    }

    @Test
    @DisplayName("Deve retornar true para isDeleted quando deletedAt estiver preenchido")
    void shouldReturnTrueWhenDeletedAtIsPresent() {
        User user = User.create("test@email.com", "encoded", "Name");

        assertFalse(user.isDeleted());

        ReflectionTestUtils.setField(user, "deletedAt", LocalDateTime.now());

        assertTrue(user.isDeleted());
    }

    @Test
    @DisplayName("Deve realizar soft delete do usuário")
    void shouldSoftDeleteUser() {
        User user = User.create("test@email.com", "encoded", "Name");

        user.softDelete();

        assertTrue(user.isDeleted());
    }

    @Test
    @DisplayName("Deve atualizar a senha do usuário")
    void shouldUpdatePassword() {
        User user = User.create("test@email.com", "oldPassword", "Name");

        user.updatePassword("newEncodedPassword");

        assertEquals("newEncodedPassword", user.getPassword());
    }

    @Test
    @DisplayName("Deve restaurar um usuário a partir de dados existentes")
    void shouldRestoreUser() {
        var id = UUID.randomUUID();
        var now = LocalDateTime.now();
        User user = User.restore(id, "test@email.com", "encoded", "Name",
                List.of(UserRole.USER, UserRole.ATTENDANT), null, now, now);

        assertEquals(id, user.getId());
        assertEquals("test@email.com", user.getEmail());
        assertEquals("encoded", user.getPassword());
        assertEquals("Name", user.getName());
        assertEquals(2, user.getRoles().size());
        assertEquals(now, user.getDateCreated());
        assertEquals(now, user.getDateUpdated());
    }
}
