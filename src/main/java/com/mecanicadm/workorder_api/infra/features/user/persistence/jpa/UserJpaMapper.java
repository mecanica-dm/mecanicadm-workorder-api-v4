package com.mecanicadm.workorder_api.infra.features.user.persistence.jpa;

import com.mecanicadm.workorder_api.core.user.domain.User;
import com.mecanicadm.workorder_api.infra.features.user.persistence.entity.UserJpaEntity;

public class UserJpaMapper {

    public static UserJpaEntity toEntity(User user) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(user.getId());
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPassword());
        entity.setName(user.getName());
        entity.setRoles(user.getRoles());

        entity.setDateCreated(user.getDateCreated());
        entity.setDateUpdated(user.getDateUpdated());
        entity.setDeletedAt(user.getDeletedAt());
        
        return entity;
    }

    public static void updateEntity(UserJpaEntity entity, User user) {
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPassword());
        entity.setName(user.getName());
        entity.setRoles(user.getRoles());
        entity.setDateUpdated(user.getDateUpdated());
    }

    public static User toDomain(UserJpaEntity entity) {
        return User.restore(
                entity.getId(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getName(),
                entity.getRoles(),
                entity.getDeletedAt(),
                entity.getDateCreated(),
                entity.getDateUpdated()
        );
    }
}
