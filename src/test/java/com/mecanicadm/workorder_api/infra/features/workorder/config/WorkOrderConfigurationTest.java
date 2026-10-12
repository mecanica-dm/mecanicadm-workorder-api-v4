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
import com.mecanicadm.workorder_api.core.workorder.usecase.*;
import com.mecanicadm.workorder_api.core.shared.domain.port.EmailService;
import com.mecanicadm.workorder_api.infra.pdf.PdfGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WorkOrderConfigurationTest {

    @Mock
    private WorkOrderGateway workOrderGateway;
    @Mock
    private WorkOrderLaborItemGateway workOrderLaborItemGateway;
    @Mock
    private WorkOrderMaterialItemGateway workOrderMaterialItemGateway;
    @Mock
    private ClientGateway clientGateway;
    @Mock
    private VehicleGateway vehicleGateway;
    @Mock
    private LaborGateway laborGateway;
    @Mock
    private MaterialGateway materialGateway;
    @Mock
    private DeductStockUseCase deductStockUseCase;
    @Mock
    private SoftDeleteStockUseCase softDeleteStockUseCase;
    @Mock
    private StockMovementsGateway stockMovementsGateway;
    @Mock
    private PdfGenerator pdfGenerator;
    @Mock
    private AddLaborToWorkOrderUseCase addLaborToWorkOrderUseCase;
    @Mock
    private AddMaterialToWorkOrderUseCase addMaterialToWorkOrderUseCase;
    @Mock
    private BudgetDecisionTokenGateway budgetDecisionTokenGateway;
    @Mock
    private EmailService emailService;
    @Mock
    private DecideWorkOrderBudgetUseCase decideWorkOrderBudgetUseCase;

    private WorkOrderConfiguration configuration;

    @BeforeEach
    void setUp() {
        configuration = new WorkOrderConfiguration();
    }

    @Test
    @DisplayName("Deve criar bean CreateWorkOrderUseCase")
    void shouldCreateCreateWorkOrderUseCase() {
        assertNotNull(configuration.createWorkOrderUseCase(workOrderGateway, addLaborToWorkOrderUseCase, addMaterialToWorkOrderUseCase));
    }

    @Test
    @DisplayName("Deve criar bean UpdateWorkOrderUseCase")
    void shouldCreateUpdateWorkOrderUseCase() {
        assertNotNull(configuration.updateWorkOrderUseCase(workOrderGateway));
    }

    @Test
    @DisplayName("Deve criar bean GetWorkOrderByIdUseCase")
    void shouldCreateGetWorkOrderByIdUseCase() {
        assertNotNull(configuration.getWorkOrderByIdUseCase(workOrderGateway));
    }

    @Test
    @DisplayName("Deve criar bean GetWorkOrderStatusUseCase")
    void shouldCreateGetWorkOrderStatusUseCase() {
        assertNotNull(configuration.getWorkOrderStatusUseCase(workOrderGateway));
    }

    @Test
    @DisplayName("Deve criar bean GetAllWorkOrderUseCase")
    void shouldCreateGetAllWorkOrderUseCase() {
        assertNotNull(configuration.getAllWorkOrderUseCase(workOrderGateway));
    }

    @Test
    @DisplayName("Deve criar bean SoftDeleteWorkOrderUseCase")
    void shouldCreateSoftDeleteWorkOrderUseCase() {
        assertNotNull(configuration.softDeleteWorkOrderUseCase(workOrderGateway, workOrderMaterialItemGateway, stockMovementsGateway));
    }

    @Test
    @DisplayName("Deve criar bean CalculateWorkOrderBudgetUseCase")
    void shouldCreateCalculateWorkOrderBudgetUseCase() {
        assertNotNull(configuration.calculateWorkOrderBudgetUseCase(workOrderGateway));
    }

    @Test
    @DisplayName("Deve criar bean DiagnoseWorkOrderUseCase")
    void shouldCreateDiagnoseWorkOrderUseCase() {
        assertNotNull(configuration.diagnoseWorkOrderUseCase(workOrderGateway, clientGateway, vehicleGateway, null));
    }

    @Test
    @DisplayName("Deve criar bean DecideWorkOrderBudgetUseCase")
    void shouldCreateDecideWorkOrderBudgetUseCase() {
        assertNotNull(configuration.decideWorkOrderBudgetUseCase(workOrderGateway));
    }

    @Test
    @DisplayName("Deve criar bean CreateWorkOrderLaborItemUseCase")
    void shouldCreateCreateWorkOrderLaborItemUseCase() {
        assertNotNull(configuration.createWorkOrderLaborItemUseCase(laborGateway));
    }

    @Test
    @DisplayName("Deve criar bean CreateWorkOrderMaterialItemUseCase")
    void shouldCreateCreateWorkOrderMaterialItemUseCase() {
        assertNotNull(configuration.createWorkOrderMaterialItemUseCase(materialGateway, deductStockUseCase));
    }

    @Test
    @DisplayName("Deve criar bean AddLaborToWorkOrderUseCase")
    void shouldCreateAddLaborToWorkOrderUseCase() {
        assertNotNull(configuration.addLaborToWorkOrderUseCase(workOrderGateway, workOrderLaborItemGateway, null));
    }

    @Test
    @DisplayName("Deve criar bean AddMaterialToWorkOrderUseCase")
    void shouldCreateAddMaterialToWorkOrderUseCase() {
        assertNotNull(configuration.addMaterialToWorkOrderUseCase(workOrderGateway, workOrderMaterialItemGateway, null));
    }

    @Test
    @DisplayName("Deve criar bean DeliverWorkOrderUseCase")
    void shouldCreateDeliverWorkOrderUseCase() {
        assertNotNull(configuration.deliverWorkOrderUseCase(workOrderGateway));
    }

    @Test
    @DisplayName("Deve criar bean FinishLaborExecutionUseCase")
    void shouldCreateFinishLaborExecutionUseCase() {
        assertNotNull(configuration.finishLaborExecutionUseCase(workOrderGateway, workOrderLaborItemGateway));
    }

    @Test
    @DisplayName("Deve criar bean FinishWorkOrderExecutionUseCase")
    void shouldCreateFinishWorkOrderExecutionUseCase() {
        assertNotNull(configuration.finishWorkOrderExecutionUseCase(workOrderGateway));
    }

    @Test
    @DisplayName("Deve criar bean GetAllWorkOrderExecutionTimeReportUseCase")
    void shouldCreateGetAllWorkOrderExecutionTimeReportUseCase() {
        assertNotNull(configuration.getAllWorkOrderExecutionTimeReportUseCase(workOrderGateway));
    }

    @Test
    @DisplayName("Deve criar bean GetPrintableBudgetUseCase")
    void shouldCreateGetPrintableBudgetUseCase() {
        assertNotNull(configuration.getPrintableBudgetUseCase(workOrderGateway, clientGateway, vehicleGateway, laborGateway, materialGateway, pdfGenerator));
    }

    @Test
    @DisplayName("Deve criar bean GetWorkOrderLaborItemByIdUseCase")
    void shouldCreateGetWorkOrderLaborItemByIdUseCase() {
        assertNotNull(configuration.getWorkOrderLaborItemByIdUseCase(workOrderGateway));
    }

    @Test
    @DisplayName("Deve criar bean ManuallyAdjustWorkOrderBudgetUseCase")
    void shouldCreateManuallyAdjustWorkOrderBudgetUseCase() {
        assertNotNull(configuration.manuallyAdjustWorkOrderBudgetUseCase(workOrderGateway));
    }

    @Test
    @DisplayName("Deve criar bean RecordWorkOrderPaymentUseCase")
    void shouldCreateRecordWorkOrderPaymentUseCase() {
        assertNotNull(configuration.recordWorkOrderPaymentUseCase(workOrderGateway));
    }

    @Test
    @DisplayName("Deve criar bean RemoveLaborItemFromWorkOrderUseCase")
    void shouldCreateRemoveLaborItemFromWorkOrderUseCase() {
        assertNotNull(configuration.removeLaborItemFromWorkOrderUseCase(workOrderLaborItemGateway));
    }

    @Test
    @DisplayName("Deve criar bean RemoveMaterialItemFromWorkOrderUseCase")
    void shouldCreateRemoveMaterialItemFromWorkOrderUseCase() {
        assertNotNull(configuration.removeMaterialItemFromWorkOrderUseCase(workOrderGateway, workOrderMaterialItemGateway, softDeleteStockUseCase));
    }

    @Test
    @DisplayName("Deve criar bean SendWorkOrderBudgetUseCase")
    void shouldCreateSendWorkOrderBudgetUseCase() {
        assertNotNull(configuration.sendWorkOrderBudgetUseCase(workOrderGateway, budgetDecisionTokenGateway, clientGateway, emailService, configuration.getPrintableBudgetUseCase(workOrderGateway, clientGateway, vehicleGateway, laborGateway, materialGateway, pdfGenerator), "/budget-decision/"));
    }

    @Test
    @DisplayName("Deve criar bean ProcessBudgetDecisionByTokenUseCase")
    void shouldCreateProcessBudgetDecisionByTokenUseCase() {
        assertNotNull(configuration.processBudgetDecisionByTokenUseCase(budgetDecisionTokenGateway, decideWorkOrderBudgetUseCase));
    }

    @Test
    @DisplayName("Deve criar bean StartLaborExecutionUseCase")
    void shouldCreateStartLaborExecutionUseCase() {
        assertNotNull(configuration.startLaborExecutionUseCase(workOrderGateway, workOrderLaborItemGateway));
    }

    @Test
    @DisplayName("Deve criar bean StartWorkOrderExecutionUseCase")
    void shouldCreateStartWorkOrderExecutionUseCase() {
        assertNotNull(configuration.startWorkOrderExecutionUseCase(workOrderGateway));
    }
}
