package com.mecanicadm.workorder_api.core.labor.domain.port;

import com.mecanicadm.workorder_api.core.labor.domain.Labor;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface LaborGateway {
    Labor create(Labor labor);

    Labor update(Labor labor);

    Optional<Labor> findById(UUID id);

    LaborPageResult findAll(LaborPageQuery query);

    boolean existsById(UUID id);

    List<Labor> findAllByIds(Set<UUID> ids);
}

