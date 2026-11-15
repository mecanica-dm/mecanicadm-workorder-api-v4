package com.mecanicadm.workorder_api.bdd;

import com.mecanicadm.workorder_api.core.workorder.usecase.AddLaborToWorkOrderUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.AuthorizeWorkOrderUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.CancelWorkOrderUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.CreateWorkOrderUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.DeliverWorkOrderUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.DiagnoseWorkOrderUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.FinishLaborExecutionUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.FinishWorkOrderExecutionUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.ReleaseWorkOrderVehicleUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.RequestWorkOrderChangesUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.StartLaborExecutionUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.StartWorkOrderExecutionUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.UpdateWorkOrderUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.AddLaborToWorkOrderCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.AuthorizeWorkOrderCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.CancelWorkOrderCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.CreateWorkOrderCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.CreateWorkOrderMaterialItemCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.DeliverWorkOrderCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.DiagnoseWorkOrderCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.FinishLaborExecutionCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.FinishWorkOrderExecutionCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.ReleaseWorkOrderVehicleCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.RequestWorkOrderChangesCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.StartLaborExecutionCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.StartWorkOrderExecutionCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.UpdateWorkOrderCommand;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.shared.exception.DomainExceptionCore;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class WorkorderStepDefinitions {

    private static final UUID CLIENT = UUID.fromString("770e8400-e29b-41d4-a716-446655440001");
    private static final String PLATE = "SAG-0001";
    private static final UUID MATERIAL = UUID.fromString("770e8400-e29b-41d4-a716-446655440020");
    private static final Map<String, UUID> LABORS = Map.of(
            "Troca de oleo", UUID.fromString("770e8400-e29b-41d4-a716-446655440010"),
            "Alinhamento", UUID.fromString("770e8400-e29b-41d4-a716-446655440011"));

    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private WorkOrderGateway gateway;
    @Autowired
    private CreateWorkOrderUseCase create;
    @Autowired
    private UpdateWorkOrderUseCase update;
    @Autowired
    private AddLaborToWorkOrderUseCase addLabor;
    @Autowired
    private DiagnoseWorkOrderUseCase diagnose;
    @Autowired
    private RequestWorkOrderChangesUseCase requestChanges;
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
    private CancelWorkOrderUseCase cancel;

    private UUID workOrderId;
    private Throwable lastError;

    @Before
    public void prepareDatabase() {
        new ResourceDatabasePopulator(new ClassPathResource("sql/saga_messaging_test_data.sql")).execute(dataSource);
        workOrderId = null;
        lastError = null;
    }

    // ---------- Dado ----------

    @Dado("que a OS foi aberta com a mão de obra {string} e {int} unidades de óleo")
    public void osAberta(String labor, int quantity) {
        workOrderId = create.execute(new CreateWorkOrderCommand(CLIENT, PLATE, "Revisao", List.of(LABORS.get(labor)),
                List.of(new CreateWorkOrderMaterialItemCommand(null, MATERIAL, quantity))));
    }

    @Dado("que a OS foi aberta sem nenhuma mão de obra")
    public void osAbertaSemMaoDeObra() {
        workOrderId = create.execute(new CreateWorkOrderCommand(CLIENT, PLATE, "Sem servicos", null, null));
    }

    @Dado("que a OS foi diagnosticada")
    public void osDiagnosticada() {
        diagnostica();
    }

    @Dado("que a OS foi autorizada")
    public void osAutorizada() {
        diagnostica();
        autoriza();
    }

    @Dado("que a OS foi cancelada")
    public void osCancelada() {
        cancela();
    }

    @Dado("que a OS foi entregue ao cliente")
    public void osEntregue() {
        diagnostica();
        autoriza();
        iniciaExecucao();
        executaMaosDeObra();
        finalizaExecucao();
        liberaVeiculo();
        entrega();
    }

    // ---------- Quando ----------

    @Quando("o mecânico diagnostica a OS")
    public void diagnostica() {
        diagnose.execute(new DiagnoseWorkOrderCommand(workOrderId));
    }

    @Quando("o mecânico tenta diagnosticar a OS")
    public void tentaDiagnosticar() {
        lastError = capture(this::diagnostica);
    }

    @Quando("a saga manda autorizar a OS")
    public void autoriza() {
        authorize.execute(new AuthorizeWorkOrderCommand(workOrderId));
    }

    @Quando("a saga tenta autorizar a OS")
    public void tentaAutorizar() {
        lastError = capture(this::autoriza);
    }

    @Quando("o mecânico inicia a execução")
    public void iniciaExecucao() {
        startExecution.execute(new StartWorkOrderExecutionCommand(workOrderId));
    }

    @Quando("o mecânico executa todas as mãos de obra")
    public void executaMaosDeObra() {
        jdbc.queryForList("SELECT id FROM work_order_labor_items WHERE work_order_id = ?", UUID.class, workOrderId)
                .forEach(item -> {
                    startLabor.execute(new StartLaborExecutionCommand(workOrderId, item));
                    finishLabor.execute(new FinishLaborExecutionCommand(workOrderId, item));
                });
    }

    @Quando("o mecânico finaliza a execução")
    public void finalizaExecucao() {
        finishExecution.execute(new FinishWorkOrderExecutionCommand(workOrderId));
    }

    @Quando("a saga manda liberar o veículo")
    public void liberaVeiculo() {
        release.execute(new ReleaseWorkOrderVehicleCommand(workOrderId));
    }

    @Quando("o veículo é entregue ao cliente")
    public void entrega() {
        deliver.execute(new DeliverWorkOrderCommand(workOrderId));
    }

    @Quando("o veículo é entregue ao cliente sem o pagamento")
    public void entregaSemPagamento() {
        lastError = capture(this::entrega);
    }

    @Quando("a saga manda registrar o pedido de alterações {string}")
    public void pedeAlteracoes(String observation) {
        requestChanges.execute(new RequestWorkOrderChangesCommand(workOrderId, observation));
    }

    @Quando("a saga tenta registrar um pedido de alterações")
    public void tentaPedirAlteracoes() {
        lastError = capture(() -> pedeAlteracoes("Qualquer alteracao"));
    }

    @Quando("o mecânico adiciona a mão de obra {string}")
    public void adicionaMaoDeObra(String labor) {
        addLabor.execute(new AddLaborToWorkOrderCommand(workOrderId, LABORS.get(labor)));
    }

    @Quando("o mecânico tenta adicionar a mão de obra {string}")
    public void tentaAdicionarMaoDeObra(String labor) {
        lastError = capture(() -> adicionaMaoDeObra(labor));
    }

    @Quando("o mecânico tenta alterar a descrição da OS")
    public void tentaAlterarDescricao() {
        lastError = capture(() -> update.execute(new UpdateWorkOrderCommand(workOrderId, null, null, "Outra descricao")));
    }

    @Quando("a saga manda cancelar a OS")
    public void cancela() {
        cancel.execute(new CancelWorkOrderCommand(workOrderId));
    }

    // ---------- Então ----------

    @Então("a OS está {string}")
    public void osEsta(String status) {
        assertEquals(status, gateway.findById(workOrderId).orElseThrow().getStatus().name());
    }

    @Então("a Workorder publicou o evento {string}")
    public void publicou(String eventType) {
        assertEquals(true, events().contains(eventType), "eventos publicados: " + events());
    }

    @Então("a Workorder publicou o evento {string} {int} vez(es)")
    public void publicouVezes(String eventType, int times) {
        assertEquals(times, events().stream().filter(eventType::equals).count(), "eventos publicados: " + events());
    }

    @Então("os eventos publicados, em ordem, são {string}")
    public void eventosEmOrdem(String expected) {
        assertEquals(Arrays.stream(expected.split(",")).map(String::trim).toList(), events());
    }

    @Então("a operação é recusada com {string}")
    public void recusada(String messageKey) {
        assertNotNull(lastError, "a operacao deveria ter sido recusada");
        assertEquals(true, lastError instanceof DomainExceptionCore, "erro inesperado: " + lastError);
        assertEquals(messageKey, lastError.getMessage());
    }

    @Então("nenhuma operação foi recusada")
    public void nadaRecusado() {
        assertNull(lastError);
    }

    @Então("o histórico de situações da OS registra a passagem por {string}")
    public void historico(String status) {
        Integer count = jdbc.queryForObject(
                "SELECT count(*) FROM work_order_status_history WHERE work_order_id = ? AND to_status = ?",
                Integer.class, workOrderId, status);
        assertEquals(1, count);
    }

    // ---------- apoio ----------

    private List<String> events() {
        return jdbc.queryForList("SELECT type FROM outbox WHERE aggregate_id = ? ORDER BY sequence", String.class, workOrderId);
    }

    private static Throwable capture(Runnable action) {
        try {
            action.run();
            return null;
        } catch (RuntimeException e) {
            return e;
        }
    }
}
