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
import com.mecanicadm.workorder_api.core.workorder.usecase.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkOrderConfiguration {

    @Bean
    public CreateWorkOrderUseCase createWorkOrderUseCase(WorkOrderGateway gateway,
                                                          AddLaborToWorkOrderUseCase addLaborToWorkOrderUseCase,
                                                          AddMaterialToWorkOrderUseCase addMaterialToWorkOrderUseCase) {
        return new CreateWorkOrderUseCase(gateway, addLaborToWorkOrderUseCase, addMaterialToWorkOrderUseCase);
    }

    @Bean
    public UpdateWorkOrderUseCase updateWorkOrderUseCase(WorkOrderGateway gateway) {
        return new UpdateWorkOrderUseCase(gateway);
    }

    @Bean
    public GetWorkOrderByIdUseCase getWorkOrderByIdUseCase(WorkOrderGateway gateway) {
        return new GetWorkOrderByIdUseCase(gateway);
    }

    @Bean
    public GetWorkOrderStatusUseCase getWorkOrderStatusUseCase(WorkOrderGateway gateway) {
        return new GetWorkOrderStatusUseCase(gateway);
    }

    @Bean
    public GetAllWorkOrderUseCase getAllWorkOrderUseCase(WorkOrderGateway gateway) {
        return new GetAllWorkOrderUseCase(gateway);
    }

    @Bean
    public SoftDeleteWorkOrderUseCase softDeleteWorkOrderUseCase(WorkOrderGateway gateway,
                                                                  WorkOrderMaterialItemGateway materialItemGateway,
                                                                  StockMovementsGateway stockMovementsGateway) {
        return new SoftDeleteWorkOrderUseCase(gateway, materialItemGateway, stockMovementsGateway);
    }

    @Bean
    public DiagnoseWorkOrderUseCase diagnoseWorkOrderUseCase(WorkOrderGateway gateway,
                                                             ClientGateway clientGateway,
                                                             VehicleGateway vehicleGateway) {
        return new DiagnoseWorkOrderUseCase(gateway, clientGateway, vehicleGateway);
    }

    @Bean
    public CreateWorkOrderLaborItemUseCase createWorkOrderLaborItemUseCase(LaborGateway laborGateway) {
        return new CreateWorkOrderLaborItemUseCase(laborGateway);
    }

    @Bean
    public CreateWorkOrderMaterialItemUseCase createWorkOrderMaterialItemUseCase(MaterialGateway materialGateway,
                                                                                 DeductStockUseCase deductStockUseCase) {
        return new CreateWorkOrderMaterialItemUseCase(materialGateway, deductStockUseCase);
    }

    @Bean
    public AddLaborToWorkOrderUseCase addLaborToWorkOrderUseCase(WorkOrderGateway gateway,
                                                                  WorkOrderLaborItemGateway laborItemGateway,
                                                                  CreateWorkOrderLaborItemUseCase createWorkOrderLaborItemUseCase) {
        return new AddLaborToWorkOrderUseCase(gateway, laborItemGateway, createWorkOrderLaborItemUseCase);
    }

    @Bean
    public AddMaterialToWorkOrderUseCase addMaterialToWorkOrderUseCase(WorkOrderGateway gateway,
                                                                        WorkOrderMaterialItemGateway materialItemGateway,
                                                                        CreateWorkOrderMaterialItemUseCase createWorkOrderMaterialItemUseCase) {
        return new AddMaterialToWorkOrderUseCase(gateway, materialItemGateway, createWorkOrderMaterialItemUseCase);
    }

    @Bean
    public DeliverWorkOrderUseCase deliverWorkOrderUseCase(WorkOrderGateway gateway) {
        return new DeliverWorkOrderUseCase(gateway);
    }

    @Bean
    public FinishLaborExecutionUseCase finishLaborExecutionUseCase(WorkOrderGateway gateway,
                                                                   WorkOrderLaborItemGateway laborItemGateway) {
        return new FinishLaborExecutionUseCase(gateway, laborItemGateway);
    }

    @Bean
    public FinishWorkOrderExecutionUseCase finishWorkOrderExecutionUseCase(WorkOrderGateway gateway) {
        return new FinishWorkOrderExecutionUseCase(gateway);
    }

    @Bean
    public GetAllWorkOrderExecutionTimeReportUseCase getAllWorkOrderExecutionTimeReportUseCase(WorkOrderGateway gateway) {
        return new GetAllWorkOrderExecutionTimeReportUseCase(gateway);
    }

    @Bean
    public GetWorkOrderLaborItemByIdUseCase getWorkOrderLaborItemByIdUseCase(WorkOrderGateway gateway) {
        return new GetWorkOrderLaborItemByIdUseCase(gateway);
    }

    @Bean
    public RemoveLaborItemFromWorkOrderUseCase removeLaborItemFromWorkOrderUseCase(WorkOrderLaborItemGateway gateway) {
        return new RemoveLaborItemFromWorkOrderUseCase(gateway);
    }

    @Bean
    public RemoveMaterialItemFromWorkOrderUseCase removeMaterialItemFromWorkOrderUseCase(WorkOrderGateway workOrderGateway,
                                                                                          WorkOrderMaterialItemGateway materialItemGateway,
                                                                                          SoftDeleteStockUseCase softDeleteStockUseCase) {
        return new RemoveMaterialItemFromWorkOrderUseCase(workOrderGateway, materialItemGateway, softDeleteStockUseCase);
    }

    @Bean
    public StartLaborExecutionUseCase startLaborExecutionUseCase(WorkOrderGateway gateway,
                                                                 WorkOrderLaborItemGateway laborItemGateway) {
        return new StartLaborExecutionUseCase(gateway, laborItemGateway);
    }

    @Bean
    public StartWorkOrderExecutionUseCase startWorkOrderExecutionUseCase(WorkOrderGateway gateway) {
        return new StartWorkOrderExecutionUseCase(gateway);
    }
}
