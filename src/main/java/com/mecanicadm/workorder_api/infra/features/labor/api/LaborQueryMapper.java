package com.mecanicadm.workorder_api.infra.features.labor.api;

import com.mecanicadm.workorder_api.core.labor.domain.port.LaborPageResult;
import com.mecanicadm.workorder_api.core.labor.usecase.query.SearchLaborsQuery;
import com.mecanicadm.workorder_api.infra.features.labor.api.dto.response.LaborResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class LaborQueryMapper {

    private LaborQueryMapper() {
    }

    public static SearchLaborsQuery toQuery(String name, Pageable pageable) {
        var sort = pageable.getSort().get().findFirst();
        var sortBy = sort.map(Sort.Order::getProperty).orElse("name");
        var direction = sort.map(s -> s.getDirection().name()).orElse("ASC");
        return new SearchLaborsQuery(name, pageable.getPageNumber(), pageable.getPageSize(), sortBy, direction);
    }

    public static Page<LaborResponse> toPage(LaborPageResult result, Pageable pageable) {
        var content = result.items().stream().map(LaborResponse::from).toList();
        return new PageImpl<>(content, pageable, result.totalElements());
    }
}
