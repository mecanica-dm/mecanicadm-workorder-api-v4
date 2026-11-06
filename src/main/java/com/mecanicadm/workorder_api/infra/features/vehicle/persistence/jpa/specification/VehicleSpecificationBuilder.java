package com.mecanicadm.workorder_api.infra.features.vehicle.persistence.jpa.specification;

import com.mecanicadm.workorder_api.core.vehicle.domain.port.VehicleFilter;
import com.mecanicadm.workorder_api.infra.features.vehicle.persistence.entity.VehicleJpaEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class VehicleSpecificationBuilder {

    private VehicleSpecificationBuilder() {
    }

    public static Specification<VehicleJpaEntity> buildFilterSpecification(VehicleFilter filter) {
        return (root, q, cb) -> {
            if (isNull(filter)) {
                return cb.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();
            addLicensePlateFilter(predicates, filter, root, cb);
            addModelFilter(predicates, filter, root, cb);
            addBrandFilter(predicates, filter, root, cb);
            addModelYearFilter(predicates, filter, root, cb);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static void addLicensePlateFilter(List<Predicate> predicates, VehicleFilter filter, Root<VehicleJpaEntity> root, CriteriaBuilder cb) {
        if (nonNull(filter.licensePlate()) && !filter.licensePlate().isBlank()) {
            String searchTerm = "%" + filter.licensePlate().toLowerCase() + "%";
            predicates.add(cb.like(cb.lower(root.get("licensePlate")), searchTerm));
        }
    }

    private static void addModelFilter(List<Predicate> predicates, VehicleFilter filter, Root<VehicleJpaEntity> root, CriteriaBuilder cb) {
        if (nonNull(filter.model()) && !filter.model().isBlank()) {
            String searchTerm = "%" + filter.model().toLowerCase() + "%";
            predicates.add(cb.like(cb.lower(root.get("model")), searchTerm));
        }
    }

    private static void addBrandFilter(List<Predicate> predicates, VehicleFilter filter, Root<VehicleJpaEntity> root, CriteriaBuilder cb) {
        if (nonNull(filter.brand()) && !filter.brand().isBlank()) {
            String searchTerm = "%" + filter.brand().toLowerCase() + "%";
            predicates.add(cb.like(cb.lower(root.get("brand")), searchTerm));
        }
    }

    private static void addModelYearFilter(List<Predicate> predicates, VehicleFilter filter, Root<VehicleJpaEntity> root, CriteriaBuilder cb) {
        if (nonNull(filter.modelYear())) {
            predicates.add(cb.equal(root.get("modelYear"), filter.modelYear()));
        }
    }
}
