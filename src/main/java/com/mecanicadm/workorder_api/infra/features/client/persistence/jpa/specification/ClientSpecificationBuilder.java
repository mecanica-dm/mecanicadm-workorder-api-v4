package com.mecanicadm.workorder_api.infra.features.client.persistence.jpa.specification;

import com.mecanicadm.workorder_api.core.client.domain.port.ClientFilter;
import com.mecanicadm.workorder_api.infra.features.client.persistence.entity.ClientJpaEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

public class ClientSpecificationBuilder {

    private ClientSpecificationBuilder() {
    }

    public static Specification<ClientJpaEntity> buildFilterSpecification(ClientFilter filter) {
        return (root, q, cb) -> {
            if (isNull(filter)) {
                return cb.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();
            addNameFilter(predicates, filter, root, cb);
            addDocumentFilter(predicates, filter, root, cb);
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static void addNameFilter(List<Predicate> predicates, ClientFilter filter, Root<ClientJpaEntity> root, CriteriaBuilder cb) {
        if (filter.name() != null && !filter.name().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + filter.name().toLowerCase() + "%"));
            }
    }

    private static void addDocumentFilter(List<Predicate> predicates, ClientFilter filter, Root<ClientJpaEntity> root, CriteriaBuilder cb) {
        if (filter.document() != null && !filter.document().isBlank()) {
            predicates.add(cb.equal(root.get("document"), filter.document()));
        }
    }
}
