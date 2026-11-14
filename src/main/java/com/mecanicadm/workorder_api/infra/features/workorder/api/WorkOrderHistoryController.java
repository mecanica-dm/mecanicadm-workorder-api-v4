package com.mecanicadm.workorder_api.infra.features.workorder.api;

import com.mecanicadm.workorder_api.core.workorder.usecase.GetWorkOrderHistoryUseCase;
import com.mecanicadm.workorder_api.core.workorder.usecase.query.GetWorkOrderHistoryQuery;
import com.mecanicadm.workorder_api.infra.features.workorder.api.dto.response.WorkOrderStatusChangeResponse;
import com.mecanicadm.workorder_api.infra.features.workorder.api.openapi.WorkOrderHistoryOpenApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/work-orders")
public class WorkOrderHistoryController implements WorkOrderHistoryOpenApi {

    private final GetWorkOrderHistoryUseCase getWorkOrderHistoryUseCase;

    public WorkOrderHistoryController(GetWorkOrderHistoryUseCase getWorkOrderHistoryUseCase) {
        this.getWorkOrderHistoryUseCase = getWorkOrderHistoryUseCase;
    }

    @Override
    @GetMapping("/{id}/history")
    public ResponseEntity<List<WorkOrderStatusChangeResponse>> findHistory(@PathVariable UUID id) {
        var history = getWorkOrderHistoryUseCase.execute(new GetWorkOrderHistoryQuery(id));
        return ResponseEntity.ok(history.stream().map(WorkOrderStatusChangeResponse::from).toList());
    }
}
