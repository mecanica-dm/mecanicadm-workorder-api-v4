package com.mecanicadm.workorder_api.infra.messaging;

import com.mecanicadm.workorder_api.core.workorder.usecase.AddLaborToWorkOrderUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.AddMaterialToWorkOrderUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.AuthorizeWorkOrderUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.CreateWorkOrderUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.DeliverWorkOrderUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.DiagnoseWorkOrderUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.FinishLaborExecutionUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.FinishWorkOrderExecutionUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.ReleaseWorkOrderVehicleUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.StartLaborExecutionUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.StartWorkOrderExecutionUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.UpdateWorkOrderUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.AddLaborToWorkOrderCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.AddMaterialToWorkOrderCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.AuthorizeWorkOrderCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.CreateWorkOrderCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.CreateWorkOrderMaterialItemCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.DeliverWorkOrderCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.DiagnoseWorkOrderCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.FinishLaborExecutionCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.FinishWorkOrderExecutionCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.ReleaseWorkOrderVehicleCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.StartLaborExecutionCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.StartWorkOrderExecutionCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.UpdateWorkOrderCommand;
import com.mecanicadm.workorder_api.core.labor.exception.LaborExceptions;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.testutils.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Sql("/sql/saga_messaging_test_data.sql")
class WorkOrderEventEmissionIT extends AbstractIntegrationTest {
    private static final UUID CLIENT = UUID.fromString("770e8400-e29b-41d4-a716-446655440001");
    private static final UUID LABOR_1 = UUID.fromString("770e8400-e29b-41d4-a716-446655440010");
    private static final UUID LABOR_2 = UUID.fromString("770e8400-e29b-41d4-a716-446655440011");
    private static final UUID MATERIAL = UUID.fromString("770e8400-e29b-41d4-a716-446655440020");

    @Autowired
    private CreateWorkOrderUseCase create;
    @Autowired
    private UpdateWorkOrderUseCase update;
    @Autowired
    private AddLaborToWorkOrderUseCase addLabor;
    @Autowired
    private AddMaterialToWorkOrderUseCase addMaterial;
    @Autowired
    private DiagnoseWorkOrderUseCase diagnose;
    @Autowired
    private AuthorizeWorkOrderUseCase authorize;
    @Autowired
    private StartWorkOrderExecutionUseCase startExecution;
    @Autowired
    private StartLaborExecutionUseCase startLabor;
    @Autowired
    private FinishLaborExecutionUseCase finishLabor;
    @Autowired
    private FinishWorkOrderExecutionUseCase finishExecution;
    @Autowired
    private ReleaseWorkOrderVehicleUseCase release;
    @Autowired
    private DeliverWorkOrderUseCase deliver;
    @Autowired
    private JdbcTemplate jdbc;

    private List<Map<String, Object>> outbox(UUID workOrderId) {
        return jdbc.queryForList("SELECT type, payload::text AS payload FROM outbox WHERE aggregate_id = ? ORDER BY sequence", workOrderId);
    }

    @Test
    @DisplayName("Deve gravar um evento por etapa do ciclo da OS, em ordem")
    void shouldWriteOneEventPerStepInOrder() {
        UUID id = create.execute(new CreateWorkOrderCommand(CLIENT, "SAG-0001", "Revisao", List.of(LABOR_1),
                List.of(new CreateWorkOrderMaterialItemCommand(null, MATERIAL, 2))));
        update.execute(new UpdateWorkOrderCommand(id, null, null, "Revisao completa"));
        addLabor.execute(new AddLaborToWorkOrderCommand(id, LABOR_2));
        addMaterial.execute(new AddMaterialToWorkOrderCommand(id, MATERIAL, 1));
        diagnose.execute(new DiagnoseWorkOrderCommand(id));
        authorize.execute(new AuthorizeWorkOrderCommand(id));
        startExecution.execute(new StartWorkOrderExecutionCommand(id));
        List<UUID> laborItems = jdbc.queryForList("SELECT id FROM work_order_labor_items WHERE work_order_id = ?", UUID.class, id);
        laborItems.forEach(item -> {
            startLabor.execute(new StartLaborExecutionCommand(id, item));
            finishLabor.execute(new FinishLaborExecutionCommand(id, item));
        });
        finishExecution.execute(new FinishWorkOrderExecutionCommand(id));
        release.execute(new ReleaseWorkOrderVehicleCommand(id));
        deliver.execute(new DeliverWorkOrderCommand(id));

        List<Map<String, Object>> events = outbox(id);
        assertEquals(List.of(
                "WorkOrderCreatedEvent", "WorkOrderUpdatedEvent", "WorkOrderUpdatedEvent", "WorkOrderUpdatedEvent",
                "WorkOrderDiagnosedEvent", "WorkOrderAuthorizedEvent", "WorkOrderExecutionStartedEvent",
                "WorkOrderExecutionFinishedEvent", "WorkOrderVehicleReleasedEvent", "WorkOrderVehicleDeliveredEvent"),
                events.stream().map(e -> (String) e.get("type")).toList());
        events.forEach(e -> {
            String json = (String) e.get("payload");
            assertTrue(json.contains("\"workOrderId\": \"" + id + "\""), "Toda mensagem deve levar o workOrderId");
            assertFalse(json.contains("\"eventType\""), "O eventType vai so no header");
            assertFalse(json.contains("\"messageId\""), "O messageId vai so no header");
        });

        String created = (String) events.get(0).get("payload");
        assertTrue(created.contains(LABOR_1.toString()), "Created deve conter a mao de obra");
        assertTrue(created.contains(MATERIAL.toString()), "Created deve conter o material");
        assertTrue(created.contains("\"Troca de oleo\"") && created.contains("80.00"), "Created deve conter nome e preco da mao de obra");
        assertTrue(created.contains("\"Oleo 5W30\"") && created.contains("\"Brand X\"") && created.contains("100.00"),
                "Created deve conter nome, marca e valor total do material (2 x 50.00)");
        assertTrue(created.contains("\"quantity\": 2"), "Created deve conter a quantidade do material");
        assertTrue(created.contains("\"Cliente Saga\"") && created.contains("cliente.saga@test.com")
                && created.contains("11122233344") && created.contains("48999999911"), "Created deve conter os dados do cliente");
        assertTrue(created.contains("\"SAG-0001\"") && created.contains("\"Onix LT\"") && created.contains("\"Chevrolet\"")
                && created.contains("2020"), "Created deve conter os dados do veiculo");
        String updated = (String) events.get(1).get("payload");
        assertTrue(updated.contains("\"Cliente Saga\"") && updated.contains("\"Onix LT\""), "Updated deve conter cliente e veiculo");
        String lastUpdated = (String) events.get(3).get("payload");
        assertTrue(lastUpdated.contains(LABOR_2.toString()), "Updated deve conter o retrato atual com a mao de obra adicionada");
    }

    @Test
    @DisplayName("Nao deve deixar OS nem evento quando a criacao falhar no meio da transacao")
    void shouldLeaveNoWorkOrderNorEventWhenCreationFailsMidway() {
        int ordersBefore = jdbc.queryForObject("SELECT count(*) FROM work_orders", Integer.class);

        assertThrows(LaborExceptions.LaborNotFound.class, () -> create.execute(
                new CreateWorkOrderCommand(CLIENT, "SAG-0001", "Revisao", List.of(UUID.randomUUID()), null)));

        assertEquals(ordersBefore, jdbc.queryForObject("SELECT count(*) FROM work_orders", Integer.class));
        assertEquals(0, jdbc.queryForObject("SELECT count(*) FROM outbox", Integer.class));
    }

    @Test
    @DisplayName("Nao deve gravar evento quando a regra de negocio impedir a transicao")
    void shouldWriteNoEventWhenBusinessRuleBlocksTransition() {
        UUID id = create.execute(new CreateWorkOrderCommand(CLIENT, "SAG-0001", "Sem servicos", null, null));

        assertThrows(WorkOrderExceptions.LaborItemsRequired.class, () -> diagnose.execute(new DiagnoseWorkOrderCommand(id)));

        assertEquals(List.of("WorkOrderCreatedEvent"),
                outbox(id).stream().map(e -> (String) e.get("type")).toList());
    }
}
