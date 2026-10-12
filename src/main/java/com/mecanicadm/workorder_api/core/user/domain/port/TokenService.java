package com.mecanicadm.workorder_api.core.user.domain.port;

public interface TokenService {
    String generateToken(String subject);
    String validateToken(String token);
    TokenClaims decodeToken(String token);
}
