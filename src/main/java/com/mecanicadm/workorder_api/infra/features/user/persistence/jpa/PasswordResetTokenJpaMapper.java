package com.mecanicadm.workorder_api.infra.features.user.persistence.jpa;

import com.mecanicadm.workorder_api.core.user.domain.PasswordResetToken;
import com.mecanicadm.workorder_api.infra.features.user.persistence.entity.PasswordResetTokenJpaEntity;

public class PasswordResetTokenJpaMapper {

    public static PasswordResetTokenJpaEntity toEntity(PasswordResetToken token) {
        PasswordResetTokenJpaEntity entity = new PasswordResetTokenJpaEntity();
        entity.setId(token.getId());
        entity.setToken(token.getToken());
        entity.setUser(UserJpaMapper.toEntity(token.getUser()));
        entity.setExpiryDate(token.getExpiryDate());
        return entity;
    }

    public static PasswordResetToken toDomain(PasswordResetTokenJpaEntity entity) {
        return new PasswordResetToken(
                entity.getId(),
                entity.getToken(),
                UserJpaMapper.toDomain(entity.getUser()),
                entity.getExpiryDate()
        );
    }
}
