package com.mecanicadm.workorder_api.infra.features.client.api.dto.response;

import com.mecanicadm.workorder_api.core.client.domain.Client;

import java.util.UUID;

public record ClientResponse(
        UUID id,
        String name,
        String email,
        String document,
        String phone
) {
    public static ClientResponse from(Client client) {
        return new ClientResponse(client.getId(), client.getName(), client.getEmail(), client.getDocument(), client.getPhone());
    }
}
