package com.mecanicadm.workorder_api.testutils;

import com.mecanicadm.workorder_api.core.user.domain.User;
import com.mecanicadm.workorder_api.core.user.domain.enums.UserRole;
import com.mecanicadm.workorder_api.infra.security.UserAdapter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    public static final String MOCK_USER_ID = "550e8400-e29b-41d4-a716-446655440001";

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        User user = User.create("mockuser@example.com", "dummy-password", "Mock User");
        ReflectionTestUtils.setField(user, "id", UUID.fromString(MOCK_USER_ID));
        
        if (!customUser.role().equals("USER")) {
            user.addRole(UserRole.valueOf(customUser.role()));
        }

        UserAdapter userAdapter = new UserAdapter(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, userAdapter.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }
}
