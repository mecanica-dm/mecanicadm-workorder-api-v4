package com.mecanicadm.workorder_api.core.client.domain.port;

import com.mecanicadm.workorder_api.core.client.domain.Client;

import java.util.List;

public record ClientPageResult(List<Client> items, long totalElements) {
}
