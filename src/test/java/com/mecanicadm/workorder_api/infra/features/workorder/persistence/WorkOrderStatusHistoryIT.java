package com.mecanicadm.workorder_api.infra.features.workorder.persistence;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderStatusChange;
import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderStatusHistoryGateway;
import com.mecanicadm.workorder_api.core.workorder.usecase.AuthorizeWorkOrderUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.CancelWorkOrderUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.AuthorizeWorkOrderCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.CancelWorkOrderCommand;
import com.mecanicadm.workorder_api.testutils.AbstractIntegrationTest;
import com.mecanicadm.workorder_api.testutils.WithMockCustomUser;
import com.mecanicadm.workorder_api.core.client.domain.Client;
import com.mecanicadm.workorder_api.core.user.domain.User;
import com.mecanicadm.workorder_api.infra.security.UserAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql("/sql/saga_messaging_test_data.sql")
class WorkOrderStatusHistoryIT extends AbstractIntegrationTest {

    private static final UUID DIAGNOSED = UUID.fromString("770e8400-e29b-41d4-a716-446655440102");
    private static final UUID IN_EXECUTION = UUID.fromString("770e8400-e29b-41d4-a716-446655440101");

    @Autowired
    private AuthorizeWorkOrderUseCase authorizeWorkOrderUseCase;
    @Autowired
    private CancelWorkOrderUseCase cancelWorkOrderUseCase;
    @Autowired
    private WorkOrderStatusHistoryGateway historyGateway;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void cleanHistory() {
        jdbc.update("DELETE FROM work_order_status_history");
    }

    @Test
    @DisplayName("Deve gravar a mudanca de status feita por um command, com autor 'saga' e na mesma transacao da OS")
    void shouldRecordCommandChangeWithSagaActor() {
        authorizeWorkOrderUseCase.execute(new AuthorizeWorkOrderCommand(DIAGNOSED));

        List<WorkOrderStatusChange> history = historyGateway.findByWorkOrderId(DIAGNOSED);

        assertEquals(1, history.size());
        assertEquals(WorkOrderStatus.DIAGNOSED, history.get(0).fromStatus());
        assertEquals(WorkOrderStatus.AWAITING_EXECUTION, history.get(0).toStatus());
        assertEquals("saga", history.get(0).changedBy());
    }

    @Test
    @WithMockCustomUser
    @DisplayName("Deve gravar como autor o e-mail do usuario autenticado quando a mudanca vem da API")
    void shouldRecordAuthenticatedUserAsActor() {
        cancelWorkOrderUseCase.execute(new CancelWorkOrderCommand(IN_EXECUTION));

        List<WorkOrderStatusChange> history = historyGateway.findByWorkOrderId(IN_EXECUTION);

        assertEquals(1, history.size());
        assertEquals(WorkOrderStatus.IN_EXECUTION, history.get(0).fromStatus());
        assertEquals(WorkOrderStatus.CANCELLED, history.get(0).toStatus());
        assertEquals("mockuser@example.com", history.get(0).changedBy());
    }

    @Test
    @DisplayName("Com o principal de producao (UserAdapter), deve gravar o e-mail do usuario como autor")
    void shouldRecordUserAdapterPrincipalAsActor() {
        User user = User.create("maria@oficina.com", "dummy-password", "Maria");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(new UserAdapter(user), null, new UserAdapter(user).getAuthorities()));
        try {
            cancelWorkOrderUseCase.execute(new CancelWorkOrderCommand(IN_EXECUTION));
        } finally {
            SecurityContextHolder.clearContext();
        }

        assertEquals("maria@oficina.com", historyGateway.findByWorkOrderId(IN_EXECUTION).get(0).changedBy());
    }

    @Test
    @DisplayName("Com um token de cliente, deve gravar o nome do cliente como autor")
    void shouldRecordClientPrincipalAsActor() {
        Client client = Client.create("Frota ABC", "contato@abc.com", "79013046002", "48999999001");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(client, null, List.of()));
        try {
            cancelWorkOrderUseCase.execute(new CancelWorkOrderCommand(IN_EXECUTION));
        } finally {
            SecurityContextHolder.clearContext();
        }

        assertEquals("cliente: Frota ABC", historyGateway.findByWorkOrderId(IN_EXECUTION).get(0).changedBy());
    }

    @Test
    @WithMockCustomUser
    @DisplayName("GET /work-orders/{id}/history deve devolver as mudancas em ordem, e 404 para OS inexistente")
    void shouldServeHistoryEndpoint() throws Exception {
        jdbc.update("INSERT INTO work_order_status_history (work_order_id, from_status, to_status, reason, changed_by, changed_at) VALUES "
                + "(?, NULL, 'RECEIVED', NULL, 'maria@teste.com', now() - interval '2 hours'), "
                + "(?, 'RECEIVED', 'CHANGES_REQUESTED', 'Trocar o filtro', 'saga', now() - interval '1 hour')", DIAGNOSED, DIAGNOSED);

        mockMvc.perform(get("/work-orders/{id}/history", DIAGNOSED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].to").value("RECEIVED"))
                .andExpect(jsonPath("$[0].changedBy").value("maria@teste.com"))
                .andExpect(jsonPath("$[1].from").value("RECEIVED"))
                .andExpect(jsonPath("$[1].to").value("CHANGES_REQUESTED"))
                .andExpect(jsonPath("$[1].reason").value("Trocar o filtro"));

        mockMvc.perform(get("/work-orders/{id}/history", UUID.randomUUID())).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("O endpoint de historico deve exigir autenticacao")
    void shouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/work-orders/{id}/history", DIAGNOSED)).andExpect(status().isUnauthorized());
    }
}
