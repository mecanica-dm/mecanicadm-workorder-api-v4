package com.mecanicadm.workorder_api.core.labor.domain.port;

import com.mecanicadm.workorder_api.core.labor.domain.Labor;

import java.util.List;

public record LaborPageResult(List<Labor> items, long totalElements) {
}

