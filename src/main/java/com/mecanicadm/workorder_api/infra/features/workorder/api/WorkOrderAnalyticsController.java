package com.mecanicadm.workorder_api.infra.features.workorder.api;

import com.mecanicadm.workorder_api.core.labor.usecase.GetAllLaborExecutionTimeReportUseCase;
import com.mecanicadm.workorder_api.core.labor.usecase.query.GetAllLaborExecutionTimeReportQuery;
import com.mecanicadm.workorder_api.core.workorder.usecase.GetAllWorkOrderExecutionTimeReportUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.query.GetAllWorkOrderExecutionTimeReportQuery;
import com.mecanicadm.workorder_api.infra.features.labor.api.dto.response.LaborExecutionReportResponse;
import com.mecanicadm.workorder_api.infra.features.workorder.api.dto.response.WorkOrderExecutionReportResponse;
import com.mecanicadm.workorder_api.infra.features.workorder.api.openapi.WorkOrderAnalyticsOpenApi;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/analytics")
public class WorkOrderAnalyticsController implements WorkOrderAnalyticsOpenApi {

    private final GetAllWorkOrderExecutionTimeReportUseCase getAllWorkOrderExecutionTimeReportUseCase;
    private final GetAllLaborExecutionTimeReportUseCase getAllLaborExecutionTimeReportUseCase;

    public WorkOrderAnalyticsController(GetAllWorkOrderExecutionTimeReportUseCase getAllWorkOrderExecutionTimeReportUseCase,
                                        GetAllLaborExecutionTimeReportUseCase getAllLaborExecutionTimeReportUseCase) {
        this.getAllWorkOrderExecutionTimeReportUseCase = getAllWorkOrderExecutionTimeReportUseCase;
        this.getAllLaborExecutionTimeReportUseCase = getAllLaborExecutionTimeReportUseCase;
    }

    @Override
    @GetMapping("/work-orders/execution-time")
    public ResponseEntity<WorkOrderExecutionReportResponse> getWorkOrderExecutionReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate initialDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate finalDate) {
        return ResponseEntity.ok(getAllWorkOrderExecutionTimeReportUseCase.execute(new GetAllWorkOrderExecutionTimeReportQuery(initialDate, finalDate)));
    }

    @Override
    @GetMapping("/labors/execution-time")
    public ResponseEntity<LaborExecutionReportResponse> getLaborExecutionReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate initialDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate finalDate) {
        var report = getAllLaborExecutionTimeReportUseCase.execute(new GetAllLaborExecutionTimeReportQuery(initialDate, finalDate));
        return ResponseEntity.ok(report);
    }
}
