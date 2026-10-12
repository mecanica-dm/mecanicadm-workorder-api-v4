package com.mecanicadm.workorder_api.infra.security;

import com.mecanicadm.workorder_api.core.client.domain.Client;
import com.mecanicadm.workorder_api.core.client.domain.port.ClientGateway;
import com.mecanicadm.workorder_api.core.user.domain.User;
import com.mecanicadm.workorder_api.core.user.domain.enums.TokenRole;
import com.mecanicadm.workorder_api.core.user.domain.port.TokenClaims;
import com.mecanicadm.workorder_api.core.user.domain.port.TokenService;
import com.mecanicadm.workorder_api.core.user.domain.port.UserGateway;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityFilterTest {

    private SecurityFilter securityFilter;
    private TokenService tokenService;
    private UserGateway userGateway;
    private ClientGateway clientGateway;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        tokenService = mock(TokenService.class);
        userGateway = mock(UserGateway.class);
        clientGateway = mock(ClientGateway.class);
        securityFilter = new SecurityFilter(tokenService, userGateway, clientGateway);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve autenticar o usuário quando um token válido é fornecido")
    void shouldAuthenticateUserWhenValidTokenProvided() throws Exception {
        String token = "valid-token";
        String email = "user@test.com";
        User user = User.create(email, "encodedPassword", "Test User");
        UserAdapter userAdapter = new UserAdapter(user);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenService.decodeToken(token)).thenReturn(new TokenClaims(email, TokenRole.USER));
        when(userGateway.findByEmail(email)).thenReturn(Optional.of(user));

        securityFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(userAdapter, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve autenticar o cliente quando o token tem role CLIENT")
    void shouldAuthenticateClientWhenTokenHasClientRole() throws Exception {
        String token = "client-token";
        String document = "529.982.247-25";
        Client client = Client.restore(
                java.util.UUID.randomUUID(), "Cliente Teste", "cliente@teste.com",
                "52998224725", "48999999000",
                java.time.LocalDateTime.now(), java.time.LocalDateTime.now(), null);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenService.decodeToken(token)).thenReturn(new TokenClaims(document, TokenRole.CLIENT));
        when(clientGateway.findByDocument("52998224725")).thenReturn(Optional.of(client));

        securityFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(client, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(clientGateway).findByDocument("52998224725");
        verify(userGateway, never()).findByEmail(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve retornar 401 quando o token é inválido")
    void shouldReturn401WhenTokenIsInvalid() throws Exception {
        String token = "invalid-token";
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenService.decodeToken(token)).thenThrow(new RuntimeException("Token inválido"));
        when(response.getWriter()).thenReturn(writer);

        securityFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve continuar a cadeia de filtros sem autenticar quando não há token")
    void shouldContinueFilterChainWhenNoTokenProvided() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        securityFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve continuar a cadeia de filtros se o usuário do token não for encontrado")
    void shouldContinueFilterChainWhenUserNotFound() throws Exception {
        String token = "valid-token";
        String email = "notfound@test.com";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenService.decodeToken(token)).thenReturn(new TokenClaims(email, TokenRole.USER));
        when(userGateway.findByEmail(email)).thenReturn(Optional.empty());

        securityFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve continuar a cadeia de filtros se o cliente do token não for encontrado")
    void shouldContinueFilterChainWhenClientNotFound() throws Exception {
        String token = "client-token";
        String document = "52998224725";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenService.decodeToken(token)).thenReturn(new TokenClaims(document, TokenRole.CLIENT));
        when(clientGateway.findByDocument(document)).thenReturn(Optional.empty());

        securityFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}