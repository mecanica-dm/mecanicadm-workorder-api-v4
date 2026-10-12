package com.mecanicadm.workorder_api.infra.security;

import com.mecanicadm.workorder_api.core.user.domain.User;
import com.mecanicadm.workorder_api.core.user.domain.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserAdapterTest {

    @Test
    @DisplayName("Deve mapear corretamente os dados do User para UserDetails")
    void shouldMapUserDetailsCorrectly() {
        User user = User.create("test@example.com", "encoded_password", "Test User");
        user.addRole(UserRole.MECHANIC);
        user.addRole(UserRole.ATTENDANT);
        UserAdapter adapter = new UserAdapter(user);

        assertEquals("test@example.com", adapter.getUsername());
        assertEquals("encoded_password", adapter.getPassword());
        
        Collection<? extends GrantedAuthority> authorities = adapter.getAuthorities();
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ATTENDANT")));
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_MECHANIC")));
        
        assertTrue(adapter.isAccountNonExpired());
        assertTrue(adapter.isAccountNonLocked());
        assertTrue(adapter.isCredentialsNonExpired());
        assertTrue(adapter.isEnabled());
    }

    @Test
    @DisplayName("Deve retornar isEnabled falso se o usuário estiver deletado")
    void shouldReturnDisabledWhenUserIsDeleted() {
        User user = User.create("test@example.com", "encoded_password", "Test User");
        ReflectionTestUtils.setField(user, "deletedAt", LocalDateTime.now());
        UserAdapter adapter = new UserAdapter(user);

        assertFalse(adapter.isEnabled());
    }
}
