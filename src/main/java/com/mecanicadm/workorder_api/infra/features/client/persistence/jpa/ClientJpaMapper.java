package com.mecanicadm.workorder_api.infra.features.client.persistence.jpa;

import com.mecanicadm.workorder_api.core.client.domain.Client;
import com.mecanicadm.workorder_api.infra.features.client.persistence.entity.ClientJpaEntity;

public class ClientJpaMapper {

    public static Client toDomain(ClientJpaEntity entity) {
        return Client.restore(entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getDocument(),
                entity.getPhone(),
                entity.getDateCreated(),
                entity.getDateUpdated(),
                entity.getDeletedAt()
        );
    }

    public static ClientJpaEntity toEntity(Client domain) {
        ClientJpaEntity entity = new ClientJpaEntity(domain.getId(),
                domain.getName(),
                domain.getEmail(),
                domain.getDocument(),
                domain.getPhone()
        );
        entity.setDateCreated(domain.getDateCreated());
        entity.setDateUpdated(domain.getDateUpdated());
        entity.setDeletedAt(domain.getDeletedAt());
        return entity;
    }
}
