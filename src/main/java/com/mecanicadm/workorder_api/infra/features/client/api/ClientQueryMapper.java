package com.mecanicadm.workorder_api.infra.features.client.api;

import com.mecanicadm.workorder_api.core.client.domain.port.ClientPageResult;
import com.mecanicadm.workorder_api.core.client.usecase.query.GetAllClientQuery;
import com.mecanicadm.workorder_api.infra.features.client.api.dto.response.ClientResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class ClientQueryMapper {

    private ClientQueryMapper() {
    }

    public static GetAllClientQuery toQuery(String name, String document, Pageable pageable) {
        var sort = pageable.getSort().stream().findFirst();
        return new GetAllClientQuery(
                name,
                document,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort.map(Sort.Order::getProperty).orElse("name"),
                sort.map(Sort.Order::getDirection).map(Enum::name).orElse("ASC")
        );
    }

    public static Page<ClientResponse> toPage(ClientPageResult result, Pageable pageable) {
        var items = result.items().stream().map(ClientResponse::from).toList();
        return new PageImpl<>(items, pageable, result.totalElements());
    }
}
