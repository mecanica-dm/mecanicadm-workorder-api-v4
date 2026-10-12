package com.mecanicadm.workorder_api.core.workorder.usecase.dto;

import java.math.BigDecimal;
import java.util.List;

public record PrintableBudgetDTO(
        PrintableClientDTO client,
        PrintableVehicleDTO vehicle,
        List<PrintableLaborItemDTO> laborItems,
        List<PrintableMaterialItemDTO> materialItems,
        BigDecimal totalLaborPrice,
        BigDecimal totalMaterialPrice,
        BigDecimal totalBudgetPrice
) {}
