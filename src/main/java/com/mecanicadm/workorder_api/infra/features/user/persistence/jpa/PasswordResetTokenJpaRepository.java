package com.mecanicadm.workorder_api.infra.features.user.persistence.jpa;

import com.mecanicadm.workorder_api.infra.features.user.persistence.entity.PasswordResetTokenJpaEntity;
import com.mecanicadm.workorder_api.infra.features.user.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetTokenJpaRepository extends JpaRepository<PasswordResetTokenJpaEntity, UUID> {
    Optional<PasswordResetTokenJpaEntity> findByToken(String token);
    
    @Modifying
    @Query("delete from PasswordResetTokenJpaEntity t where t.user = ?1")
    void deleteByUser(UserJpaEntity user);
}
