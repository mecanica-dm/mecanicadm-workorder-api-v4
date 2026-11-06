package com.mecanicadm.workorder_api.infra.features.labor.persistence.jpa;

import com.mecanicadm.workorder_api.core.labor.domain.Labor;
import com.mecanicadm.workorder_api.core.labor.domain.port.LaborGateway;
import com.mecanicadm.workorder_api.core.labor.domain.port.LaborPageQuery;
import com.mecanicadm.workorder_api.core.labor.domain.port.LaborPageResult;
import com.mecanicadm.workorder_api.infra.features.labor.persistence.entity.LaborJpaEntity;
import com.mecanicadm.workorder_api.infra.features.labor.persistence.jpa.specification.LaborSpecificationBuilder;
import com.mecanicadm.workorder_api.shared.exception.TechnicalException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static java.util.Objects.isNull;

@Repository
public class LaborRepositoryImpl implements LaborGateway {

    private final LaborJpaRepository jpaRepository;

    public LaborRepositoryImpl(LaborJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Labor create(Labor labor) {
        if (isNull(labor)) {
            throw new TechnicalException("error.technical.entity.null", "Labor", "criação");
        }
        LaborJpaEntity saved = jpaRepository.save(LaborJpaMapper.toEntity(labor));
        return LaborJpaMapper.toDomain(saved);
    }

    @Override
    public Labor update(Labor labor) {
        if (isNull(labor)) {
            throw new TechnicalException("error.technical.entity.null", "Labor", "atualização");
        }
        LaborJpaEntity entity = LaborJpaMapper.toEntity(labor);
        LaborJpaEntity saved = jpaRepository.save(entity);
        return LaborJpaMapper.toDomain(saved);
    }

    @Override
    public Optional<Labor> findById(UUID id) {
        return jpaRepository.findById(id).map(LaborJpaMapper::toDomain);
    }

    @Override
    public LaborPageResult findAll(LaborPageQuery query) {
        Specification<LaborJpaEntity> spec = LaborSpecificationBuilder.buildFilterSpecification(query.filter());
        Sort sort = Sort.by(Sort.Direction.fromString(query.direction()), query.sortBy());
        Pageable pageable = PageRequest.of(query.page(), query.size(), sort);
        var page = jpaRepository.findAll(spec, pageable);

        return new LaborPageResult(
                page.map(LaborJpaMapper::toDomain).getContent(),
                page.getTotalElements()
        );
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public List<Labor> findAllByIds(Set<UUID> ids) {
        return jpaRepository.findAllByIds(ids).stream().map(LaborJpaMapper::toDomain).toList();
    }
}

