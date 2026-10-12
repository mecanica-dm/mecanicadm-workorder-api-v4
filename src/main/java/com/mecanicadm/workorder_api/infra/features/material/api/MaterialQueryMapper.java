package com.mecanicadm.workorder_api.infra.features.material.api;

import com.mecanicadm.workorder_api.core.material.domain.enums.MaterialType;
import com.mecanicadm.workorder_api.core.material.domain.port.MaterialPageResult;
import com.mecanicadm.workorder_api.core.material.usecase.query.SearchMaterialsQuery;
import com.mecanicadm.workorder_api.infra.features.material.api.dto.response.MaterialResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class MaterialQueryMapper {

    private MaterialQueryMapper() {
    }

    public static SearchMaterialsQuery toQuery(String name, String brand, MaterialType type, Pageable pageable) {
        var sort = pageable.getSort().get().findFirst();
        var sortBy = sort.map(Sort.Order::getProperty).orElse("name");
        var direction = sort.map(s -> s.getDirection().name()).orElse("ASC");
        return new SearchMaterialsQuery(name, brand, type, pageable.getPageNumber(), pageable.getPageSize(), sortBy, direction);
    }

    public static Page<MaterialResponse> toPage(MaterialPageResult result, Pageable pageable) {
        var responseList = result.items().stream().map(MaterialResponse::from).toList();
        return new PageImpl<>(responseList, pageable, result.totalElements());
    }
}
