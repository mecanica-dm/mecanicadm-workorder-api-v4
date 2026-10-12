package com.mecanicadm.workorder_api.infra.features.client.persistence.jpa.specification;

import com.mecanicadm.workorder_api.core.client.domain.port.ClientFilter;
import com.mecanicadm.workorder_api.infra.features.client.persistence.entity.ClientJpaEntity;
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
class ClientSpecificationBuilderTest {

    @Mock
    private Root<ClientJpaEntity> root;
    @Mock
    private CriteriaQuery<?> query;
    @Mock
    private CriteriaBuilder cb;
    @Mock
    private Predicate conjunctionPredicate;
    @Mock
    private Predicate namePredicate;
    @Mock
    private Predicate documentPredicate;
    @Mock
    private Path namePath;
    @Mock
    private Path documentPath;
    @Mock
    private Expression lowerExpression;

    @Test
    @DisplayName("Deve retornar conjunction quando filter for nulo")
    void shouldReturnConjunctionWhenFilterIsNull() {
        when(cb.conjunction()).thenReturn(conjunctionPredicate);

        Specification<ClientJpaEntity> spec = ClientSpecificationBuilder.buildFilterSpecification(null);

        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(cb).conjunction();
        verifyNoMoreInteractions(cb);
    }

    @Test
    @DisplayName("Deve aplicar filtro por nome")
    void shouldApplyNameFilter() {
        ClientFilter filter = mock(ClientFilter.class);
        when(filter.name()).thenReturn("João");
        when(filter.document()).thenReturn(null);

        when(root.get("name")).thenReturn(namePath);
        when(cb.lower(namePath)).thenReturn(lowerExpression);
        when(cb.like(lowerExpression, "%joão%")).thenReturn(namePredicate);
        lenient().when(cb.and(any(Predicate[].class))).thenReturn(conjunctionPredicate);

        Specification<ClientJpaEntity> spec = ClientSpecificationBuilder.buildFilterSpecification(filter);
        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(cb).lower(namePath);
        verify(cb).and(namePredicate);
    }

    @Test
    @DisplayName("Deve aplicar filtro por documento")
    void shouldApplyDocumentFilter() {
        ClientFilter filter = mock(ClientFilter.class);
        when(filter.name()).thenReturn(null);
        when(filter.document()).thenReturn("123456789");

        when(root.get("document")).thenReturn(documentPath);
        when(cb.equal(documentPath, "123456789")).thenReturn(documentPredicate);
        lenient().when(cb.and(any(Predicate[].class))).thenReturn(conjunctionPredicate);

        Specification<ClientJpaEntity> spec = ClientSpecificationBuilder.buildFilterSpecification(filter);
        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(cb).equal(documentPath, "123456789");
        verify(cb).and(documentPredicate);
    }

    @Test
    @DisplayName("Deve aplicar filtro por nome e documento simultaneamente")
    void shouldApplyNameAndDocumentFilter() {
        ClientFilter filter = mock(ClientFilter.class);
        when(filter.name()).thenReturn("Maria");
        when(filter.document()).thenReturn("987654321");

        when(root.get("name")).thenReturn(namePath);
        when(cb.lower(namePath)).thenReturn(lowerExpression);
        when(cb.like(lowerExpression, "%maria%")).thenReturn(namePredicate);
        when(root.get("document")).thenReturn(documentPath);
        when(cb.equal(documentPath, "987654321")).thenReturn(documentPredicate);
        lenient().when(cb.and(any(Predicate[].class))).thenReturn(conjunctionPredicate);

        Specification<ClientJpaEntity> spec = ClientSpecificationBuilder.buildFilterSpecification(filter);
        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(cb).like(lowerExpression, "%maria%");
        verify(cb).equal(documentPath, "987654321");
        verify(cb, times(1)).and(any(Predicate[].class));
    }
}
