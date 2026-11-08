package com.mecanicadm.workorder_api.core.material.domain.port;

import com.mecanicadm.workorder_api.core.material.domain.Material;

import java.util.List;

public record MaterialPageResult(
        List<Material> items,
        long totalElements
) {
}
