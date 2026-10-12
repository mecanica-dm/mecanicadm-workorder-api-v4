package com.mecanicadm.workorder_api.infra.security;

import com.mecanicadm.workorder_api.core.client.domain.port.ClientGateway;
import com.mecanicadm.workorder_api.core.user.domain.User;
import com.mecanicadm.workorder_api.core.user.domain.enums.TokenRole;
import com.mecanicadm.workorder_api.core.user.domain.port.TokenClaims;
import com.mecanicadm.workorder_api.core.user.domain.port.TokenService;
import com.mecanicadm.workorder_api.core.user.domain.port.UserGateway;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserGateway userGateway;
    private final ClientGateway clientGateway;

    @Autowired
    public SecurityFilter(TokenService tokenService, UserGateway userGateway, ClientGateway clientGateway) {
        this.tokenService = tokenService;
        this.userGateway = userGateway;
        this.clientGateway = clientGateway;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String token = this.recoverToken(request);
        if (token != null) {
            try {
                TokenClaims claims = tokenService.decodeToken(token);
                if (TokenRole.CLIENT == claims.role()) {
                    this.authenticateClient(claims.subject());
                } else {
                    this.authenticateUser(claims.subject());
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setCharacterEncoding("UTF-8");
                response.setContentType("text/plain; charset=UTF-8");
                response.getWriter().write("Token inválido ou expirado");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void authenticateUser(String subject) {
        User user = userGateway.findByEmail(subject).orElse(null);
        if (user != null) {
            UserDetails userDetails = new UserAdapter(user);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    private void authenticateClient(String document) {
        if (document == null || document.isBlank()) {
            return;
        }

        String digits = document.replaceAll("\\D", "");
        clientGateway.findByDocument(digits).ifPresent(client ->
                SecurityContextHolder.getContext()
                        .setAuthentication(new UsernamePasswordAuthenticationToken(client, null, List.of()))
        );
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "").trim();
    }
}