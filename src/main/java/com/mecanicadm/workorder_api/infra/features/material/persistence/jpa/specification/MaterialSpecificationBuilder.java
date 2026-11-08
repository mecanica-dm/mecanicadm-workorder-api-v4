package com.mecanicadm.workorder_api.infra.features.material.persistence.jpa.specification;

import com.mecanicadm.workorder_api.core.material.domain.port.MaterialFilter;
import com.mecanicadm.workorder_api.infra.features.material.persistence.entity.MaterialJpaEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

public class MaterialSpecificationBuilder {

    private MaterialSpecificationBuilder() {
    }

    public static Specification<MaterialJpaEntity> buildFilterSpecification(MaterialFilter filter) {
        return (root, q, cb) -> {
            if (isNull(filter)) {
                return cb.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();
            addNameFilter(predicates, filter, root, cb);
            addBrandFilter(predicates, filter, root, cb);
            addTypeFilter(predicates, filter, root, cb);
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static void addNameFilter(List<Predicate> predicates, MaterialFilter filter, Root<MaterialJpaEntity> root, CriteriaBuilder cb) {
        HibernateCriteriaBuilder hcb = (HibernateCriteriaBuilder) cb;
        if (StringUtils.hasText(filter.name())) {
            predicates.add(hcb.ilike(root.get("name"), "%" + filter.name() + "%"));
        }
    }

    public static void addBrandFilter(List<Predicate> predicates, MaterialFilter filter, Root<MaterialJpaEntity> root, CriteriaBuilder cb) {
        HibernateCriteriaBuilder hcb = (HibernateCriteriaBuilder) cb;
        if (StringUtils.hasText(filter.brand())) {
            predicates.add(hcb.ilike(root.get("brand"), "%" + filter.brand() + "%"));
        }
    }

    public static void addTypeFilter(List<Predicate> predicates, MaterialFilter filter, Root<MaterialJpaEntity> root, CriteriaBuilder cb) {
        if (filter.type() != null) {
            predicates.add(cb.equal(root.get("type"), filter.type()));
        }
    }

}
