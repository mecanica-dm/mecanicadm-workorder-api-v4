package com.mecanicadm.workorder_api.infra.features.workorder.persistence.jpa.specification;

import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderFilter;
import com.mecanicadm.workorder_api.infra.features.workorder.persistence.entity.WorkOrderJpaEntity;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkOrderSpecificationBuilderTest {

    @Mock
    private Root<WorkOrderJpaEntity> root;
    @Mock
    private CriteriaQuery<?> query;
    @Mock
    private CriteriaBuilder cb;
    @Mock
    private Predicate conjunctionPredicate;
    @Mock
    private Predicate clientIdPredicate;
    @Mock
    private Predicate vehicleIdPredicate;
    @Mock
    private Path clientIdPath;
    @Mock
    private Path vehicleIdPath;
    @Mock
    private Path statusPath;
    @Mock
    private Predicate statusPredicate;

    @Test
    @DisplayName("Deve retornar conjunction quando filter for nulo")
    void shouldReturnConjunctionWhenFilterIsNull() {
        when(cb.conjunction()).thenReturn(conjunctionPredicate);

        Specification<WorkOrderJpaEntity> spec = WorkOrderSpecificationBuilder.buildFilterSpecification(null);

        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(cb).conjunction();
        verifyNoMoreInteractions(cb);
    }

    @Test
    @DisplayName("Deve aplicar filtro por clientId")
    void shouldApplyClientIdFilter() {
        UUID clientId = UUID.randomUUID();
        WorkOrderFilter filter = mock(WorkOrderFilter.class);
        when(filter.clientId()).thenReturn(clientId);
        when(filter.licensePlate()).thenReturn(null);

        when(root.get("clientId")).thenReturn(clientIdPath);
        when(cb.equal(clientIdPath, clientId)).thenReturn(clientIdPredicate);
        when(cb.and(any(Predicate[].class))).thenReturn(conjunctionPredicate);

        Specification<WorkOrderJpaEntity> spec = WorkOrderSpecificationBuilder.buildFilterSpecification(filter);
        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(cb).equal(clientIdPath, clientId);
        verify(cb).and(clientIdPredicate);
    }

    @Test
    @DisplayName("Deve aplicar filtro por licensePlate (vehicleId)")
    void shouldApplyLicensePlateFilter() {
        WorkOrderFilter filter = mock(WorkOrderFilter.class);
        when(filter.clientId()).thenReturn(null);
        when(filter.licensePlate()).thenReturn("ABC-1234");

        when(root.get("vehicleId")).thenReturn(vehicleIdPath);
        when(cb.equal(vehicleIdPath, "ABC-1234")).thenReturn(vehicleIdPredicate);
        when(cb.and(any(Predicate[].class))).thenReturn(conjunctionPredicate);

        Specification<WorkOrderJpaEntity> spec = WorkOrderSpecificationBuilder.buildFilterSpecification(filter);
        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(cb).equal(vehicleIdPath, "ABC-1234");
        verify(cb).and(vehicleIdPredicate);
    }

    @Test
    @DisplayName("Deve aplicar filtros combinados de clientId e licensePlate")
    void shouldApplyCombinedFilters() {
        UUID clientId = UUID.randomUUID();
        WorkOrderFilter filter = mock(WorkOrderFilter.class);
        when(filter.clientId()).thenReturn(clientId);
        when(filter.licensePlate()).thenReturn("XYZ-9876");

        when(root.get("clientId")).thenReturn(clientIdPath);
        when(root.get("vehicleId")).thenReturn(vehicleIdPath);
        when(cb.equal(clientIdPath, clientId)).thenReturn(clientIdPredicate);
        when(cb.equal(vehicleIdPath, "XYZ-9876")).thenReturn(vehicleIdPredicate);
        when(cb.and(any(Predicate[].class))).thenReturn(conjunctionPredicate);

        Specification<WorkOrderJpaEntity> spec = WorkOrderSpecificationBuilder.buildFilterSpecification(filter);
        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(cb).equal(clientIdPath, clientId);
        verify(cb).equal(vehicleIdPath, "XYZ-9876");
        verify(cb).and(any(Predicate[].class));
    }

    @Test
    @DisplayName("Deve retornar conjunction quando filter nao tiver filtros configurados")
    void shouldReturnConjunctionWhenFilterHasNoCriteria() {
        WorkOrderFilter filter = mock(WorkOrderFilter.class);
        when(filter.clientId()).thenReturn(null);
        when(filter.licensePlate()).thenReturn(null);

        when(cb.and(any(Predicate[].class))).thenReturn(conjunctionPredicate);

        Specification<WorkOrderJpaEntity> spec = WorkOrderSpecificationBuilder.buildFilterSpecification(filter);
        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(cb).and(any(Predicate[].class));
    }

    @Test
    @DisplayName("Deve aplicar filtro por statuses")
    void shouldApplyStatusesFilter() {
        WorkOrderFilter filter = mock(WorkOrderFilter.class);
        when(filter.clientId()).thenReturn(null);
        when(filter.licensePlate()).thenReturn(null);
        when(filter.statuses()).thenReturn(Set.of(WorkOrderStatus.RECEIVED, WorkOrderStatus.DIAGNOSED));

        when(root.get("status")).thenReturn(statusPath);
        when(statusPath.in(Set.of(WorkOrderStatus.RECEIVED, WorkOrderStatus.DIAGNOSED))).thenReturn(statusPredicate);
        when(cb.and(any(Predicate[].class))).thenReturn(conjunctionPredicate);

        Specification<WorkOrderJpaEntity> spec = WorkOrderSpecificationBuilder.buildFilterSpecification(filter);
        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(root).get("status");
        verify(cb).and(statusPredicate);
    }

    @Test
    @DisplayName("Deve aplicar filtro por clientId e statuses combinados")
    void shouldApplyClientIdAndStatusesFilters() {
        UUID clientId = UUID.randomUUID();
        WorkOrderFilter filter = mock(WorkOrderFilter.class);
        when(filter.clientId()).thenReturn(clientId);
        when(filter.licensePlate()).thenReturn(null);
        when(filter.statuses()).thenReturn(Set.of(WorkOrderStatus.RECEIVED));

        when(root.get("clientId")).thenReturn(clientIdPath);
        when(root.get("status")).thenReturn(statusPath);
        when(cb.equal(clientIdPath, clientId)).thenReturn(clientIdPredicate);
        when(statusPath.in(Set.of(WorkOrderStatus.RECEIVED))).thenReturn(statusPredicate);
        when(cb.and(any(Predicate[].class))).thenReturn(conjunctionPredicate);

        Specification<WorkOrderJpaEntity> spec = WorkOrderSpecificationBuilder.buildFilterSpecification(filter);
        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(cb).equal(clientIdPath, clientId);
        verify(root).get("status");
        verify(cb).and(any(Predicate[].class));
    }

    @Test
    @DisplayName("Deve retornar conjunction quando statuses for vazio")
    void shouldReturnConjunctionWhenStatusesIsEmpty() {
        WorkOrderFilter filter = mock(WorkOrderFilter.class);
        when(filter.clientId()).thenReturn(null);
        when(filter.licensePlate()).thenReturn(null);
        when(filter.statuses()).thenReturn(Set.of());

        when(cb.and(any(Predicate[].class))).thenReturn(conjunctionPredicate);

        Specification<WorkOrderJpaEntity> spec = WorkOrderSpecificationBuilder.buildFilterSpecification(filter);
        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(root, never()).get("status");
        verify(cb).and(any(Predicate[].class));
    }
}
