package com.mecanicadm.workorder_api.infra.features.workorder.api.dto.response;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderStatusChange;
import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Uma mudança de status da ordem de serviço")
public record WorkOrderStatusChangeResponse(
        @Schema(description = "Status anterior; vazio na criação da OS", nullable = true) WorkOrderStatus from,
        @Schema(description = "Novo status") WorkOrderStatus to,
        @Schema(description = "Motivo, quando houver (por exemplo, o pedido de alterações do cliente)", nullable = true) String reason,
        @Schema(description = "Quem mudou: o e-mail do usuário, ou saga quando a mudança vem de um command") String changedBy,
        @Schema(description = "Quando mudou") LocalDateTime changedAt) {

    public static WorkOrderStatusChangeResponse from(WorkOrderStatusChange change) {
        return new WorkOrderStatusChangeResponse(change.fromStatus(), change.toStatus(), change.reason(), change.changedBy(), change.changedAt());
    }
}
