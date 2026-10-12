package com.mecanicadm.workorder_api.core.workorder.usecase.dto;

public record PrintableBudgetResponse(
        String fileName,
        String base64Content
) {
}
