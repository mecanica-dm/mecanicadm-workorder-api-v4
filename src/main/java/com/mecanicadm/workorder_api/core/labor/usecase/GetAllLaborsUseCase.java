package com.mecanicadm.workorder_api.core.labor.usecase;

import com.mecanicadm.workorder_api.core.labor.domain.port.LaborFilter;
import com.mecanicadm.workorder_api.core.labor.domain.port.LaborGateway;
import com.mecanicadm.workorder_api.core.labor.domain.port.LaborPageQuery;
import com.mecanicadm.workorder_api.core.labor.domain.port.LaborPageResult;
import com.mecanicadm.workorder_api.core.labor.usecase.query.SearchLaborsQuery;
import com.mecanicadm.workorder_api.shared.usecase.UseCase;

public class GetAllLaborsUseCase implements UseCase<SearchLaborsQuery, LaborPageResult> {

    private final LaborGateway gateway;

    public GetAllLaborsUseCase(LaborGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public LaborPageResult execute(SearchLaborsQuery query) {
        LaborFilter filter = new LaborFilter(query.name());
        LaborPageQuery pageQuery = new LaborPageQuery(filter, query.page(), query.size(), query.sortBy(), query.direction());
        return gateway.findAll(pageQuery);
    }
}
