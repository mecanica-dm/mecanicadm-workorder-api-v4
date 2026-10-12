package com.mecanicadm.workorder_api.infra.features.user.config;

import com.mecanicadm.workorder_api.core.user.domain.port.PasswordResetTokenGateway;
import com.mecanicadm.workorder_api.core.user.domain.port.TokenService;
import com.mecanicadm.workorder_api.core.user.domain.port.UserGateway;
import com.mecanicadm.workorder_api.core.user.usecase.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserUseCaseConfig {

    @Bean
    public CreateUserUseCase createUserUseCase(UserGateway userGateway, PasswordEncoder passwordEncoder) {
        return new CreateUserUseCase(userGateway, passwordEncoder);
    }

    @Bean
    public UpdateUserUseCase updateUserUseCase(UserGateway userGateway, PasswordEncoder passwordEncoder) {
        return new UpdateUserUseCase(userGateway, passwordEncoder);
    }

    @Bean
    public SoftDeleteUserUseCase softDeleteUserUseCase(UserGateway userGateway) {
        return new SoftDeleteUserUseCase(userGateway);
    }

    @Bean
    public GetUserByIdUseCase getUserByIdUseCase(UserGateway userGateway) {
        return new GetUserByIdUseCase(userGateway);
    }

    @Bean
    public AuthenticateUserUseCase authenticateUserUseCase(UserGateway userGateway, PasswordEncoder passwordEncoder, TokenService tokenService) {
        return new AuthenticateUserUseCase(userGateway, passwordEncoder, tokenService);
    }

    @Bean
    public RequestPasswordResetUseCase requestPasswordResetUseCase(UserGateway userGateway, PasswordResetTokenGateway passwordResetTokenGateway) {
        return new RequestPasswordResetUseCase(userGateway, passwordResetTokenGateway);
    }

    @Bean
    public ResetPasswordUseCase resetPasswordUseCase(UserGateway userGateway, PasswordResetTokenGateway passwordResetTokenGateway, PasswordEncoder passwordEncoder) {
        return new ResetPasswordUseCase(userGateway, passwordResetTokenGateway, passwordEncoder);
    }
}
