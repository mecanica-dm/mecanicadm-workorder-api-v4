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
import org.junit.jupiter.api.BeforeEach;
import org.springframework.transaction.PlatformTransactionManager;
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
    private EmailService emailService;
    @Mock
    private StockMovementsGateway stockMovementsGateway;
    @Mock
    private AddLaborToWorkOrderUseCase addLaborToWorkOrderUseCase;
    @Mock
    private AddMaterialToWorkOrderUseCase addMaterialToWorkOrderUseCase;
    @Mock
    private PlatformTransactionManager transactionManager;

    private WorkOrderConfiguration configuration;

    @BeforeEach
    void setUp() {
        configuration = new WorkOrderConfiguration();
    }

    @Test
    @DisplayName("Deve criar bean CreateWorkOrderUseCase")
    void shouldCreateWorkOrderUseCase() {
        assertNotNull(configuration.createWorkOrderUseCase(workOrderGateway, addLaborToWorkOrderUseCase, addMaterialToWorkOrderUseCase, null, transactionManager));
    }

    @Test
    @DisplayName("Deve criar bean UpdateWorkOrderUseCase")
    void shouldCreateUpdateWorkOrderUseCase() {
        assertNotNull(configuration.updateWorkOrderUseCase(workOrderGateway, null, transactionManager));
    }

    @Test
    @DisplayName("Deve criar bean RequestWorkOrderChangesUseCase")
    void shouldCreateRequestWorkOrderChangesUseCase() {
        assertNotNull(configuration.requestWorkOrderChangesUseCase(workOrderGateway, null, transactionManager));
    }

    @Test
    @DisplayName("Deve criar bean ReleaseWorkOrderVehicleUseCase")
    void shouldCreateReleaseWorkOrderVehicleUseCase() {
        assertNotNull(configuration.releaseWorkOrderVehicleUseCase(workOrderGateway, null, transactionManager));
    }

    @Test
    @DisplayName("Deve criar bean CancelWorkOrderUseCase")
    void shouldCreateCancelWorkOrderUseCase() {
        assertNotNull(configuration.cancelWorkOrderUseCase(workOrderGateway, stockMovementsGateway, null, transactionManager));
    }

    @Test
    @DisplayName("Deve criar bean AuthorizeWorkOrderUseCase")
    void shouldCreateAuthorizeWorkOrderUseCase() {
        assertNotNull(configuration.authorizeWorkOrderUseCase(workOrderGateway, null, transactionManager));
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
    @DisplayName("Deve criar bean DiagnoseWorkOrderUseCase")
    void shouldCreateDiagnoseWorkOrderUseCase() {
        assertNotNull(configuration.diagnoseWorkOrderUseCase(workOrderGateway, clientGateway, vehicleGateway, null, transactionManager));
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
        assertNotNull(configuration.addLaborToWorkOrderUseCase(workOrderGateway, workOrderLaborItemGateway, null, null, transactionManager));
    }

    @Test
    @DisplayName("Deve criar bean AddMaterialToWorkOrderUseCase")
    void shouldCreateAddMaterialToWorkOrderUseCase() {
        assertNotNull(configuration.addMaterialToWorkOrderUseCase(workOrderGateway, workOrderMaterialItemGateway, null, null, transactionManager));
    }

    @Test
    @DisplayName("Deve criar bean DeliverWorkOrderUseCase")
    void shouldCreateDeliverWorkOrderUseCase() {
        assertNotNull(configuration.deliverWorkOrderUseCase(workOrderGateway, null, transactionManager));
    }

    @Test
    @DisplayName("Deve criar bean FinishLaborExecutionUseCase")
    void shouldCreateFinishLaborExecutionUseCase() {
        assertNotNull(configuration.finishLaborExecutionUseCase(workOrderGateway, workOrderLaborItemGateway));
    }

    @Test
    @DisplayName("Deve criar bean FinishWorkOrderExecutionUseCase")
    void shouldCreateFinishWorkOrderExecutionUseCase() {
        assertNotNull(configuration.finishWorkOrderExecutionUseCase(workOrderGateway, null, transactionManager));
    }

    @Test
    @DisplayName("Deve criar bean GetAllWorkOrderExecutionTimeReportUseCase")
    void shouldCreateGetAllWorkOrderExecutionTimeReportUseCase() {
        assertNotNull(configuration.getAllWorkOrderExecutionTimeReportUseCase(workOrderGateway));
    }

    @Test
    @DisplayName("Deve criar bean GetWorkOrderLaborItemByIdUseCase")
    void shouldCreateGetWorkOrderLaborItemByIdUseCase() {
        assertNotNull(configuration.getWorkOrderLaborItemByIdUseCase(workOrderGateway));
    }

    @Test
    @DisplayName("Deve criar bean RemoveLaborItemFromWorkOrderUseCase")
    void shouldCreateRemoveLaborItemFromWorkOrderUseCase() {
        assertNotNull(configuration.removeLaborItemFromWorkOrderUseCase(workOrderLaborItemGateway, workOrderGateway, null, transactionManager));
    }

    @Test
    @DisplayName("Deve criar bean RemoveMaterialItemFromWorkOrderUseCase")
    void shouldCreateRemoveMaterialItemFromWorkOrderUseCase() {
        assertNotNull(configuration.removeMaterialItemFromWorkOrderUseCase(workOrderGateway, workOrderMaterialItemGateway, softDeleteStockUseCase, null, transactionManager));
    }

    @Test
    @DisplayName("Deve criar bean StartLaborExecutionUseCase")
    void shouldCreateStartLaborExecutionUseCase() {
        assertNotNull(configuration.startLaborExecutionUseCase(workOrderGateway, workOrderLaborItemGateway));
    }

    @Test
    @DisplayName("Deve criar bean StartWorkOrderExecutionUseCase")
    void shouldCreateStartWorkOrderExecutionUseCase() {
        assertNotNull(configuration.startWorkOrderExecutionUseCase(workOrderGateway, null, transactionManager));
    }
}
