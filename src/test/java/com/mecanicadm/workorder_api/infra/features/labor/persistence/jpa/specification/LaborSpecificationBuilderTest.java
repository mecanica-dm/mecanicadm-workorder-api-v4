package com.mecanicadm.workorder_api.infra.features.labor.persistence.jpa.specification;

import com.mecanicadm.workorder_api.core.labor.domain.port.LaborFilter;
import com.mecanicadm.workorder_api.infra.features.labor.persistence.entity.LaborJpaEntity;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LaborSpecificationBuilderTest {

    @Mock
    private Root<LaborJpaEntity> root;
    @Mock
    private CriteriaQuery<?> query;
    @Mock
    private CriteriaBuilder cb;
    @Mock
    private Predicate conjunctionPredicate;
    @Mock
    private Predicate namePredicate;
    @Mock
    private Path namePath;
    @Mock
    private Expression lowerExpression;

    @Test
    @DisplayName("Deve retornar conjunction quando filter for nulo")
    void shouldReturnConjunctionWhenFilterIsNull() {
        when(cb.conjunction()).thenReturn(conjunctionPredicate);

        Specification<LaborJpaEntity> spec = LaborSpecificationBuilder.buildFilterSpecification(null);

        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(cb).conjunction();
        verifyNoMoreInteractions(cb);
    }

    @Test
    @DisplayName("Deve aplicar filtro por nome")
    void shouldApplyNameFilter() {
        LaborFilter filter = mock(LaborFilter.class);
        when(filter.name()).thenReturn("Troca de Óleo");

        when(root.get("name")).thenReturn(namePath);
        when(cb.lower(namePath)).thenReturn(lowerExpression);
        when(cb.like(lowerExpression, "%troca de óleo%")).thenReturn(namePredicate);
        lenient().when(cb.and(any(Predicate[].class))).thenReturn(conjunctionPredicate);

        Specification<LaborJpaEntity> spec = LaborSpecificationBuilder.buildFilterSpecification(filter);
        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(cb).lower(namePath);
        verify(cb).and(namePredicate);
    }

    @Test
    @DisplayName("Deve retornar conjunction quando filter tiver nome vazio")
    void shouldReturnConjunctionWhenFilterHasEmptyName() {
        LaborFilter filter = mock(LaborFilter.class);
        when(filter.name()).thenReturn("");

        lenient().when(cb.and(any(Predicate[].class))).thenReturn(conjunctionPredicate);

        Specification<LaborJpaEntity> spec = LaborSpecificationBuilder.buildFilterSpecification(filter);
        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(cb).and(any(Predicate[].class));
    }

    @Test
    @DisplayName("Deve retornar conjunction quando filter tiver nome em branco")
    void shouldReturnConjunctionWhenFilterHasBlankName() {
        LaborFilter filter = mock(LaborFilter.class);
        when(filter.name()).thenReturn("   ");

        lenient().when(cb.and(any(Predicate[].class))).thenReturn(conjunctionPredicate);

        Specification<LaborJpaEntity> spec = LaborSpecificationBuilder.buildFilterSpecification(filter);
        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(cb).and(any(Predicate[].class));
    }
}
