package com.mecanicadm.workorder_api.infra.features.vehicle.api;

import com.mecanicadm.workorder_api.core.vehicle.domain.port.VehiclePageResult;
import com.mecanicadm.workorder_api.core.vehicle.usecase.query.GetAllVehiclesQuery;
import com.mecanicadm.workorder_api.infra.features.vehicle.api.dto.response.VehicleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class VehicleQueryMapper {

    private VehicleQueryMapper() {
    }

    public static GetAllVehiclesQuery toQuery(String licensePlate, String model, String brand, Short modelYear, Pageable pageable) {
        var sort = pageable.getSort().get().findFirst();
        var sortBy = sort.map(Sort.Order::getProperty).orElse("licensePlate");
        var direction = sort.map(s -> s.getDirection().name()).orElse("ASC");
        return new GetAllVehiclesQuery(licensePlate, model, brand, modelYear, pageable.getPageNumber(), pageable.getPageSize(), sortBy, direction);
    }

    public static Page<VehicleResponse> toPage(VehiclePageResult result, Pageable pageable) {
        var items = result.items().stream().map(VehicleResponse::from).toList();
        return new PageImpl<>(items, pageable, result.totalElements());
    }
}
