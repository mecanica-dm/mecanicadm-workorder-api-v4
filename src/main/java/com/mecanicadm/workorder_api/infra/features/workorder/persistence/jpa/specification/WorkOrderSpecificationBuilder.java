package com.mecanicadm.workorder_api.infra.features.workorder.persistence.jpa.specification;

import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderFilter;
import com.mecanicadm.workorder_api.infra.features.workorder.persistence.entity.WorkOrderJpaEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.*;

public class WorkOrderSpecificationBuilder {

    private WorkOrderSpecificationBuilder() {
    }

    public static Specification<WorkOrderJpaEntity> buildFilterSpecification(WorkOrderFilter filter) {
        return (root, query, cb) -> {
            if (isNull(filter)) {
                return cb.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();
            addClientId(predicates, filter, root, cb);
            addVehicleId(predicates, filter, root, cb);
            addStatuses(predicates, filter, root);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static void addClientId(List<Predicate> predicates, WorkOrderFilter filter, Root<WorkOrderJpaEntity> root, CriteriaBuilder cb) {
        if (nonNull(filter.clientId())) {
            predicates.add(cb.equal(root.get("clientId"), filter.clientId()));
        }
    }

    private static void addVehicleId(List<Predicate> predicates, WorkOrderFilter filter, Root<WorkOrderJpaEntity> root, CriteriaBuilder cb) {
        if (StringUtils.hasText(filter.licensePlate())) {
            predicates.add(cb.equal(root.get("vehicleId"), filter.licensePlate()));
        }
    }

    private static void addStatuses(List<Predicate> predicates, WorkOrderFilter filter, Root<WorkOrderJpaEntity> root) {
        if (nonNull(filter.statuses()) && !filter.statuses().isEmpty()) {
            predicates.add(root.get("status").in(filter.statuses()));
        }
    }
}
