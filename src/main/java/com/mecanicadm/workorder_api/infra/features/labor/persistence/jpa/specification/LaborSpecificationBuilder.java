package com.mecanicadm.workorder_api.infra.features.labor.persistence.jpa.specification;

import com.mecanicadm.workorder_api.core.labor.domain.port.LaborFilter;
import com.mecanicadm.workorder_api.infra.features.labor.persistence.entity.LaborJpaEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class LaborSpecificationBuilder {

    private LaborSpecificationBuilder() {
    }

    public static Specification<LaborJpaEntity> buildFilterSpecification(LaborFilter filter) {
        return (root, q, cb) -> {
            if (isNull(filter)) {
                return cb.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();
            addNameFilter(predicates, filter, root, cb);
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static void addNameFilter(List<Predicate> predicates, LaborFilter filter, Root<LaborJpaEntity> root, CriteriaBuilder cb) {
        if (nonNull(filter.name()) && !filter.name().isBlank()) {
            String searchTerm = "%" + filter.name().toLowerCase() + "%";
            predicates.add(cb.like(cb.lower(root.get("name")), searchTerm));
        }
    }
}

