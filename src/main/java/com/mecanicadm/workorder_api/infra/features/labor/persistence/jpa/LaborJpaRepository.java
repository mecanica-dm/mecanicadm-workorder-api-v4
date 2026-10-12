package com.mecanicadm.workorder_api.infra.features.labor.persistence.jpa;

import com.mecanicadm.workorder_api.infra.features.labor.persistence.entity.LaborJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface LaborJpaRepository extends JpaRepository<LaborJpaEntity, UUID>, JpaSpecificationExecutor<LaborJpaEntity> {

    @Query("SELECT l FROM LaborJpaEntity l WHERE l.id IN :ids")
    List<LaborJpaEntity> findAllByIds(@Param("ids") Set<UUID> ids);
}

