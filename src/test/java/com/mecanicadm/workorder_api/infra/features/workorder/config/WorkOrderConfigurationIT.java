package com.mecanicadm.workorder_api.infra.features.workorder.config;

import com.mecanicadm.workorder_api.core.client.domain.port.ClientGateway;
import com.mecanicadm.workorder_api.core.labor.domain.port.LaborGateway;
import com.mecanicadm.workorder_api.core.material.domain.port.MaterialGateway;
import com.mecanicadm.workorder_api.core.stockmovements.domain.port.StockMovementsGateway;
import com.mecanicadm.workorder_api.core.stockmovements.usecase.DeductStockUseCase;
import com.mecanicadm.workorder_api.core.stockmovements.usecase.SoftDeleteStockUseCase;
import com.mecanicadm.workorder_api.core.vehicle.domain.port.VehicleGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderLaborItemGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderMaterialItemGateway;
import com.mecanicadm.workorder_api.core.workorder.usecase.AddLaborToWorkOrderUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.AddMaterialToWorkOrderUseCase;
import com.mecanicadm.workorder_api.core.shared.domain.port.EmailService;
import com.mecanicadm.workorder_api.testutils.AbstractIntegrationTest;
import org.springframework.transaction.PlatformTransactionManager;
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
    private EmailService emailService;
    @MockitoBean
    private StockMovementsGateway stockMovementsGateway;
    @MockitoBean
    private AddLaborToWorkOrderUseCase addLaborToWorkOrderUseCase;
    @MockitoBean
    private AddMaterialToWorkOrderUseCase addMaterialToWorkOrderUseCase;
    @MockitoBean
    private PlatformTransactionManager transactionManager;

    @Autowired
    private WorkOrderConfiguration workOrderConfiguration;

    @Test
    @DisplayName("Deve carregar WorkOrderConfiguration no contexto")
    void shouldLoadWorkOrderConfiguration() {
        assertNotNull(workOrderConfiguration);
    }

    @Test
    @DisplayName("Deve criar bean CreateWorkOrderUseCase")
    void shouldCreateWorkOrderUseCase() {
        assertNotNull(workOrderConfiguration.createWorkOrderUseCase(workOrderGateway, addLaborToWorkOrderUseCase, addMaterialToWorkOrderUseCase, null, transactionManager));
    }

    @Test
    @DisplayName("Deve criar bean UpdateWorkOrderUseCase")
    void shouldCreateUpdateWorkOrderUseCase() {
        assertNotNull(workOrderConfiguration.updateWorkOrderUseCase(workOrderGateway, null, transactionManager));
    }

    @Test
    @DisplayName("Deve criar bean RequestWorkOrderChangesUseCase")
    void shouldCreateRequestWorkOrderChangesUseCase() {
        assertNotNull(workOrderConfiguration.requestWorkOrderChangesUseCase(workOrderGateway, null, transactionManager));
    }

    @Test
    @DisplayName("Deve criar bean ReleaseWorkOrderVehicleUseCase")
    void shouldCreateReleaseWorkOrderVehicleUseCase() {
        assertNotNull(workOrderConfiguration.releaseWorkOrderVehicleUseCase(workOrderGateway, null, transactionManager));
    }

    @Test
    @DisplayName("Deve criar bean CancelWorkOrderUseCase")
    void shouldCreateCancelWorkOrderUseCase() {
        assertNotNull(workOrderConfiguration.cancelWorkOrderUseCase(workOrderGateway, stockMovementsGateway, null, transactionManager));
    }

    @Test
    @DisplayName("Deve criar bean AuthorizeWorkOrderUseCase")
    void shouldCreateAuthorizeWorkOrderUseCase() {
        assertNotNull(workOrderConfiguration.authorizeWorkOrderUseCase(workOrderGateway, null, transactionManager));
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
    @DisplayName("Deve criar bean DiagnoseWorkOrderUseCase")
    void shouldCreateDiagnoseWorkOrderUseCase() {
        assertNotNull(workOrderConfiguration.diagnoseWorkOrderUseCase(workOrderGateway, clientGateway, vehicleGateway, null, transactionManager));
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
        assertNotNull(workOrderConfiguration.addLaborToWorkOrderUseCase(workOrderGateway, workOrderLaborItemGateway, null, null, transactionManager));
    }

    @Test
    @DisplayName("Deve criar bean AddMaterialToWorkOrderUseCase")
    void shouldCreateAddMaterialToWorkOrderUseCase() {
        assertNotNull(workOrderConfiguration.addMaterialToWorkOrderUseCase(workOrderGateway, workOrderMaterialItemGateway, null, null, transactionManager));
    }

    @Test
    @DisplayName("Deve criar bean DeliverWorkOrderUseCase")
    void shouldCreateDeliverWorkOrderUseCase() {
        assertNotNull(workOrderConfiguration.deliverWorkOrderUseCase(workOrderGateway, null, transactionManager));
    }

    @Test
    @DisplayName("Deve criar bean FinishLaborExecutionUseCase")
    void shouldCreateFinishLaborExecutionUseCase() {
        assertNotNull(workOrderConfiguration.finishLaborExecutionUseCase(workOrderGateway, workOrderLaborItemGateway));
    }

    @Test
    @DisplayName("Deve criar bean FinishWorkOrderExecutionUseCase")
    void shouldCreateFinishWorkOrderExecutionUseCase() {
        assertNotNull(workOrderConfiguration.finishWorkOrderExecutionUseCase(workOrderGateway, null, transactionManager));
    }

    @Test
    @DisplayName("Deve criar bean GetAllWorkOrderExecutionTimeReportUseCase")
    void shouldCreateGetAllWorkOrderExecutionTimeReportUseCase() {
        assertNotNull(workOrderConfiguration.getAllWorkOrderExecutionTimeReportUseCase(workOrderGateway));
    }

    @Test
    @DisplayName("Deve criar bean GetWorkOrderLaborItemByIdUseCase")
    void shouldCreateGetWorkOrderLaborItemByIdUseCase() {
        assertNotNull(workOrderConfiguration.getWorkOrderLaborItemByIdUseCase(workOrderGateway));
    }

    @Test
    @DisplayName("Deve criar bean RemoveLaborItemFromWorkOrderUseCase")
    void shouldCreateRemoveLaborItemFromWorkOrderUseCase() {
        assertNotNull(workOrderConfiguration.removeLaborItemFromWorkOrderUseCase(workOrderLaborItemGateway, workOrderGateway, null, transactionManager));
    }

    @Test
    @DisplayName("Deve criar bean RemoveMaterialItemFromWorkOrderUseCase")
    void shouldCreateRemoveMaterialItemFromWorkOrderUseCase() {
        assertNotNull(workOrderConfiguration.removeMaterialItemFromWorkOrderUseCase(workOrderGateway, workOrderMaterialItemGateway, softDeleteStockUseCase, null, transactionManager));
    }

    @Test
    @DisplayName("Deve criar bean StartLaborExecutionUseCase")
    void shouldCreateStartLaborExecutionUseCase() {
        assertNotNull(workOrderConfiguration.startLaborExecutionUseCase(workOrderGateway, workOrderLaborItemGateway));
    }

    @Test
    @DisplayName("Deve criar bean StartWorkOrderExecutionUseCase")
    void shouldCreateStartWorkOrderExecutionUseCase() {
        assertNotNull(workOrderConfiguration.startWorkOrderExecutionUseCase(workOrderGateway, null, transactionManager));
    }
}
