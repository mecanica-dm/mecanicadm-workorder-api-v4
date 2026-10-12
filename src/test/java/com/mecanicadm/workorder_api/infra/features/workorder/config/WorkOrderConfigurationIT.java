package com.mecanicadm.workorder_api.infra.features.workorder.config;

import com.mecanicadm.workorder_api.core.client.domain.port.ClientGateway;
import com.mecanicadm.workorder_api.core.labor.domain.port.LaborGateway;
import com.mecanicadm.workorder_api.core.material.domain.port.MaterialGateway;
import com.mecanicadm.workorder_api.core.stockmovements.domain.port.StockMovementsGateway;
import com.mecanicadm.workorder_api.core.stockmovements.usecase.DeductStockUseCase;
import com.mecanicadm.workorder_api.core.stockmovements.usecase.SoftDeleteStockUseCase;
import com.mecanicadm.workorder_api.core.vehicle.domain.port.VehicleGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.port.BudgetDecisionTokenGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderLaborItemGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderMaterialItemGateway;
import com.mecanicadm.workorder_api.core.workorder.usecase.AddLaborToWorkOrderUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.AddMaterialToWorkOrderUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.DecideWorkOrderBudgetUseCase;
import com.mecanicadm.workorder_api.core.shared.domain.port.EmailService;
import com.mecanicadm.workorder_api.infra.pdf.PdfGenerator;
import com.mecanicadm.workorder_api.testutils.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class WorkOrderConfigurationIT extends AbstractIntegrationTest {

    @MockitoBean
    private WorkOrderGateway workOrderGateway;
    @MockitoBean
    private WorkOrderLaborItemGateway workOrderLaborItemGateway;
    @MockitoBean
    private WorkOrderMaterialItemGateway workOrderMaterialItemGateway;
    @MockitoBean
    private ClientGateway clientGateway;
    @MockitoBean
    private VehicleGateway vehicleGateway;
    @MockitoBean
    private LaborGateway laborGateway;
    @MockitoBean
    private MaterialGateway materialGateway;
    @MockitoBean
    private DeductStockUseCase deductStockUseCase;
    @MockitoBean
    private SoftDeleteStockUseCase softDeleteStockUseCase;
    @MockitoBean
    private StockMovementsGateway stockMovementsGateway;
    @MockitoBean
    private PdfGenerator pdfGenerator;
    @MockitoBean
    private AddLaborToWorkOrderUseCase addLaborToWorkOrderUseCase;
    @MockitoBean
    private AddMaterialToWorkOrderUseCase addMaterialToWorkOrderUseCase;
    @MockitoBean
    private BudgetDecisionTokenGateway budgetDecisionTokenGateway;
    @MockitoBean
    private EmailService emailService;
    @MockitoBean
    private DecideWorkOrderBudgetUseCase decideWorkOrderBudgetUseCase;

    @Autowired
    private WorkOrderConfiguration workOrderConfiguration;

    @Test
    @DisplayName("Deve carregar WorkOrderConfiguration no contexto")
    void shouldLoadWorkOrderConfiguration() {
        assertNotNull(workOrderConfiguration);
    }

    @Test
    @DisplayName("Deve criar bean CreateWorkOrderUseCase")
    void shouldCreateCreateWorkOrderUseCase() {
        assertNotNull(workOrderConfiguration.createWorkOrderUseCase(workOrderGateway, addLaborToWorkOrderUseCase, addMaterialToWorkOrderUseCase));
    }

    @Test
    @DisplayName("Deve criar bean UpdateWorkOrderUseCase")
    void shouldCreateUpdateWorkOrderUseCase() {
        assertNotNull(workOrderConfiguration.updateWorkOrderUseCase(workOrderGateway));
    }

    @Test
    @DisplayName("Deve criar bean GetWorkOrderByIdUseCase")
    void shouldCreateGetWorkOrderByIdUseCase() {
        assertNotNull(workOrderConfiguration.getWorkOrderByIdUseCase(workOrderGateway));
    }

    @Test
    @DisplayName("Deve criar bean GetAllWorkOrderUseCase")
    void shouldCreateGetAllWorkOrderUseCase() {
        assertNotNull(workOrderConfiguration.getAllWorkOrderUseCase(workOrderGateway));
    }

    @Test
    @DisplayName("Deve criar bean GetWorkOrderStatusUseCase")
    void shouldCreateGetWorkOrderStatusUseCase() {
        assertNotNull(workOrderConfiguration.getWorkOrderStatusUseCase(workOrderGateway));
    }

    @Test
    @DisplayName("Deve criar bean SoftDeleteWorkOrderUseCase")
    void shouldCreateSoftDeleteWorkOrderUseCase() {
        assertNotNull(workOrderConfiguration.softDeleteWorkOrderUseCase(workOrderGateway, workOrderMaterialItemGateway, stockMovementsGateway));
    }

    @Test
    @DisplayName("Deve criar bean CalculateWorkOrderBudgetUseCase")
    void shouldCreateCalculateWorkOrderBudgetUseCase() {
        assertNotNull(workOrderConfiguration.calculateWorkOrderBudgetUseCase(workOrderGateway));
    }

    @Test
    @DisplayName("Deve criar bean DiagnoseWorkOrderUseCase")
    void shouldCreateDiagnoseWorkOrderUseCase() {
        assertNotNull(workOrderConfiguration.diagnoseWorkOrderUseCase(workOrderGateway, clientGateway, vehicleGateway, null));
    }

    @Test
    @DisplayName("Deve criar bean DecideWorkOrderBudgetUseCase")
    void shouldCreateDecideWorkOrderBudgetUseCase() {
        assertNotNull(workOrderConfiguration.decideWorkOrderBudgetUseCase(workOrderGateway));
    }

    @Test
    @DisplayName("Deve criar bean CreateWorkOrderLaborItemUseCase")
    void shouldCreateCreateWorkOrderLaborItemUseCase() {
        assertNotNull(workOrderConfiguration.createWorkOrderLaborItemUseCase(laborGateway));
    }

    @Test
    @DisplayName("Deve criar bean CreateWorkOrderMaterialItemUseCase")
    void shouldCreateCreateWorkOrderMaterialItemUseCase() {
        assertNotNull(workOrderConfiguration.createWorkOrderMaterialItemUseCase(materialGateway, deductStockUseCase));
    }

    @Test
    @DisplayName("Deve criar bean AddLaborToWorkOrderUseCase")
    void shouldCreateAddLaborToWorkOrderUseCase() {
        assertNotNull(workOrderConfiguration.addLaborToWorkOrderUseCase(workOrderGateway, workOrderLaborItemGateway, null));
    }

    @Test
    @DisplayName("Deve criar bean AddMaterialToWorkOrderUseCase")
    void shouldCreateAddMaterialToWorkOrderUseCase() {
        assertNotNull(workOrderConfiguration.addMaterialToWorkOrderUseCase(workOrderGateway, workOrderMaterialItemGateway, null));
    }

    @Test
    @DisplayName("Deve criar bean DeliverWorkOrderUseCase")
    void shouldCreateDeliverWorkOrderUseCase() {
        assertNotNull(workOrderConfiguration.deliverWorkOrderUseCase(workOrderGateway));
    }

    @Test
    @DisplayName("Deve criar bean FinishLaborExecutionUseCase")
    void shouldCreateFinishLaborExecutionUseCase() {
        assertNotNull(workOrderConfiguration.finishLaborExecutionUseCase(workOrderGateway, workOrderLaborItemGateway));
    }

    @Test
    @DisplayName("Deve criar bean FinishWorkOrderExecutionUseCase")
    void shouldCreateFinishWorkOrderExecutionUseCase() {
        assertNotNull(workOrderConfiguration.finishWorkOrderExecutionUseCase(workOrderGateway));
    }

    @Test
    @DisplayName("Deve criar bean GetAllWorkOrderExecutionTimeReportUseCase")
    void shouldCreateGetAllWorkOrderExecutionTimeReportUseCase() {
        assertNotNull(workOrderConfiguration.getAllWorkOrderExecutionTimeReportUseCase(workOrderGateway));
    }

    @Test
    @DisplayName("Deve criar bean GetPrintableBudgetUseCase")
    void shouldCreateGetPrintableBudgetUseCase() {
        assertNotNull(workOrderConfiguration.getPrintableBudgetUseCase(workOrderGateway, clientGateway, vehicleGateway, laborGateway, materialGateway, pdfGenerator));
    }

    @Test
    @DisplayName("Deve criar bean GetWorkOrderLaborItemByIdUseCase")
    void shouldCreateGetWorkOrderLaborItemByIdUseCase() {
        assertNotNull(workOrderConfiguration.getWorkOrderLaborItemByIdUseCase(workOrderGateway));
    }

    @Test
    @DisplayName("Deve criar bean ManuallyAdjustWorkOrderBudgetUseCase")
    void shouldCreateManuallyAdjustWorkOrderBudgetUseCase() {
        assertNotNull(workOrderConfiguration.manuallyAdjustWorkOrderBudgetUseCase(workOrderGateway));
    }

    @Test
    @DisplayName("Deve criar bean RecordWorkOrderPaymentUseCase")
    void shouldCreateRecordWorkOrderPaymentUseCase() {
        assertNotNull(workOrderConfiguration.recordWorkOrderPaymentUseCase(workOrderGateway));
    }

    @Test
    @DisplayName("Deve criar bean RemoveLaborItemFromWorkOrderUseCase")
    void shouldCreateRemoveLaborItemFromWorkOrderUseCase() {
        assertNotNull(workOrderConfiguration.removeLaborItemFromWorkOrderUseCase(workOrderLaborItemGateway));
    }

    @Test
    @DisplayName("Deve criar bean RemoveMaterialItemFromWorkOrderUseCase")
    void shouldCreateRemoveMaterialItemFromWorkOrderUseCase() {
        assertNotNull(workOrderConfiguration.removeMaterialItemFromWorkOrderUseCase(workOrderGateway, workOrderMaterialItemGateway, softDeleteStockUseCase));
    }

    @Test
    @DisplayName("Deve criar bean SendWorkOrderBudgetUseCase")
    void shouldCreateSendWorkOrderBudgetUseCase() {
        assertNotNull(workOrderConfiguration.sendWorkOrderBudgetUseCase(workOrderGateway, budgetDecisionTokenGateway, clientGateway, emailService, workOrderConfiguration.getPrintableBudgetUseCase(workOrderGateway, clientGateway, vehicleGateway, laborGateway, materialGateway, pdfGenerator), "/budget-decision/"));
    }

    @Test
    @DisplayName("Deve criar bean ProcessBudgetDecisionByTokenUseCase")
    void shouldCreateProcessBudgetDecisionByTokenUseCase() {
        assertNotNull(workOrderConfiguration.processBudgetDecisionByTokenUseCase(budgetDecisionTokenGateway, decideWorkOrderBudgetUseCase));
    }

    @Test
    @DisplayName("Deve criar bean StartLaborExecutionUseCase")
    void shouldCreateStartLaborExecutionUseCase() {
        assertNotNull(workOrderConfiguration.startLaborExecutionUseCase(workOrderGateway, workOrderLaborItemGateway));
    }

    @Test
    @DisplayName("Deve criar bean StartWorkOrderExecutionUseCase")
    void shouldCreateStartWorkOrderExecutionUseCase() {
        assertNotNull(workOrderConfiguration.startWorkOrderExecutionUseCase(workOrderGateway));
    }
}
