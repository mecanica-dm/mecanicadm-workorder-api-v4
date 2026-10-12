package com.mecanicadm.workorder_api.core.user.usecase;

import com.mecanicadm.workorder_api.core.user.domain.User;
import com.mecanicadm.workorder_api.core.user.usecase.dto.AuthenticateUserResponse;
import com.mecanicadm.workorder_api.core.user.domain.port.TokenService;
import com.mecanicadm.workorder_api.core.user.domain.port.UserGateway;
import com.mecanicadm.workorder_api.core.user.exception.UserExceptions;
import com.mecanicadm.workorder_api.core.user.usecase.query.AuthenticateUserQuery;
import com.mecanicadm.workorder_api.shared.usecase.UseCase;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthenticateUserUseCase implements UseCase<AuthenticateUserQuery, AuthenticateUserResponse> {

    private final UserGateway userGateway;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthenticateUserUseCase(UserGateway userGateway, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userGateway = userGateway;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Override
    public AuthenticateUserResponse execute(AuthenticateUserQuery query) {
        User user = userGateway.findByEmail(query.email())
                .orElseThrow(UserExceptions.BadCredentials::new);

        if (!passwordEncoder.matches(query.password(), user.getPassword())) {
            throw new UserExceptions.BadCredentials();
        }

        String token = tokenService.generateToken(user.getEmail());

        return new AuthenticateUserResponse(token, user.getId(), user.getName(), user.getEmail());
    }
}
