package com.mecanicadm.workorder_api.infra.features.user.persistence.jpa.specification;

import com.mecanicadm.workorder_api.core.user.domain.port.UserFilter;
import com.mecanicadm.workorder_api.infra.features.user.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecificationBuilder {

    public static Specification<UserJpaEntity> buildFilterSpecification(UserFilter filter) {
        if (filter == null) {
            return Specification.where(null);
        }

        return Specification
                .where(like("name", filter.name()))
                .and(like("email", filter.email()));
    }

    private static Specification<UserJpaEntity> like(String field, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get(field)), "%" + value.toLowerCase() + "%");
    }
}
