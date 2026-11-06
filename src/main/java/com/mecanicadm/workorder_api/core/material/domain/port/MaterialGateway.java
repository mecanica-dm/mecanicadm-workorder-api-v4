package com.mecanicadm.workorder_api.core.material.domain.port;

import com.mecanicadm.workorder_api.core.material.domain.Material;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface MaterialGateway {
    Material create(Material material);

    Material update(Material material);

    Optional<Material> findById(UUID id);

    boolean existsById(UUID id);

    MaterialPageResult findAll(MaterialPageQuery query);

    List<Material> findAllByIds(Set<UUID> ids);
}
