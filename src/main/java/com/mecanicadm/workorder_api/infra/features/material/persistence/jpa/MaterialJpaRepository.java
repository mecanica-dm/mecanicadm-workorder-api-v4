package com.mecanicadm.workorder_api.infra.features.material.persistence.jpa;

import com.mecanicadm.workorder_api.infra.features.material.persistence.entity.MaterialJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface MaterialJpaRepository extends JpaRepository<MaterialJpaEntity, UUID>, JpaSpecificationExecutor<MaterialJpaEntity> {
    @Query("SELECT m FROM MaterialJpaEntity m WHERE m.id = :id AND m.deletedAt IS NULL")
    Optional<MaterialJpaEntity> findActiveById(@Param("id") UUID id);

    @Query("SELECT m FROM MaterialJpaEntity m WHERE m.id IN :ids")
    List<MaterialJpaEntity> findAllByIds(@Param("ids") Set<UUID> ids);
}
