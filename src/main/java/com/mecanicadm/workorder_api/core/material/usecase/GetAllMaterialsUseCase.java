package com.mecanicadm.workorder_api.core.material.usecase;

import com.mecanicadm.workorder_api.core.material.domain.port.MaterialFilter;
import com.mecanicadm.workorder_api.core.material.domain.port.MaterialGateway;
import com.mecanicadm.workorder_api.core.material.domain.port.MaterialPageQuery;
import com.mecanicadm.workorder_api.core.material.domain.port.MaterialPageResult;
import com.mecanicadm.workorder_api.core.material.usecase.query.SearchMaterialsQuery;
import com.mecanicadm.workorder_api.shared.usecase.UseCase;

public class GetAllMaterialsUseCase implements UseCase<SearchMaterialsQuery, MaterialPageResult> {

    private final MaterialGateway gateway;

    public GetAllMaterialsUseCase(MaterialGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public MaterialPageResult execute(SearchMaterialsQuery query) {
        MaterialFilter filter = new MaterialFilter(query.name(), query.brand(), query.type());
        MaterialPageQuery pageQuery = new MaterialPageQuery(filter, query.page(), query.size(), query.sortBy(), query.direction());
        return gateway.findAll(pageQuery);
    }
}
