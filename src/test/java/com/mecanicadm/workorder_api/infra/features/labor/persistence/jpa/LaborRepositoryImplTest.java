package com.mecanicadm.workorder_api.infra.features.labor.persistence.jpa;

import com.mecanicadm.workorder_api.core.labor.domain.Labor;
import com.mecanicadm.workorder_api.core.labor.domain.port.LaborFilter;
import com.mecanicadm.workorder_api.core.labor.domain.port.LaborPageQuery;
import com.mecanicadm.workorder_api.core.labor.domain.port.LaborPageResult;
import com.mecanicadm.workorder_api.infra.features.labor.persistence.entity.LaborJpaEntity;
import com.mecanicadm.workorder_api.infra.features.labor.persistence.jpa.specification.LaborSpecificationBuilder;
import com.mecanicadm.workorder_api.shared.exception.TechnicalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LaborRepositoryImplTest {

    @Mock
    private LaborJpaRepository jpaRepository;

    private LaborRepositoryImpl repository;
    private UUID id;
    private Labor domain;
    private LaborJpaEntity entity;

    @BeforeEach
    void setUp() {
        repository = new LaborRepositoryImpl(jpaRepository);

        id = UUID.randomUUID();
        domain = mock(Labor.class);
        lenient().when(domain.getId()).thenReturn(id);

        entity = new LaborJpaEntity(id, "Labor Test", BigDecimal.TEN);
    }

    @Test
    @DisplayName("Deve criar labor com sucesso")
    void shouldCreateLaborSuccessfully() {
        try (MockedStatic<LaborJpaMapper> mapper = mockStatic(LaborJpaMapper.class)) {
            mapper.when(() -> LaborJpaMapper.toEntity(domain)).thenReturn(entity);
            mapper.when(() -> LaborJpaMapper.toDomain(entity)).thenReturn(domain);
            when(jpaRepository.save(entity)).thenReturn(entity);

            Labor result = repository.create(domain);

            assertSame(domain, result);
            verify(jpaRepository).save(entity);
        }
    }

    @Test
    @DisplayName("Deve lancar excecao ao criar labor nulo")
    void shouldThrowExceptionWhenCreatingNullLabor() {
        assertThrows(TechnicalException.class, () -> repository.create(null));
        verifyNoInteractions(jpaRepository);
    }

    @Test
    @DisplayName("Deve atualizar labor com sucesso")
    void shouldUpdateLaborSuccessfully() {
        try (MockedStatic<LaborJpaMapper> mapper = mockStatic(LaborJpaMapper.class)) {
            mapper.when(() -> LaborJpaMapper.toEntity(domain)).thenReturn(entity);
            mapper.when(() -> LaborJpaMapper.toDomain(entity)).thenReturn(domain);
            when(jpaRepository.save(entity)).thenReturn(entity);

            Labor result = repository.update(domain);

            assertSame(domain, result);
            verify(jpaRepository).save(entity);
        }
    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar labor nulo")
    void shouldThrowExceptionWhenUpdatingNullLabor() {
        assertThrows(TechnicalException.class, () -> repository.update(null));
        verifyNoInteractions(jpaRepository);
    }

    @Test
    @DisplayName("Deve buscar labor por ID com sucesso")
    void shouldFindLaborById() {
        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));

        try (MockedStatic<LaborJpaMapper> mapper = mockStatic(LaborJpaMapper.class)) {
            mapper.when(() -> LaborJpaMapper.toDomain(entity)).thenReturn(domain);

            Optional<Labor> result = repository.findById(id);

            assertTrue(result.isPresent());
            assertSame(domain, result.get());
            verify(jpaRepository).findById(id);
        }
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar labor por ID inexistente")
    void shouldReturnEmptyWhenLaborNotFoundById() {
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Labor> result = repository.findById(id);

        assertTrue(result.isEmpty());
        verify(jpaRepository).findById(id);
    }

    @Test
    @DisplayName("Deve verificar existencia por ID")
    void shouldCheckExistenceById() {
        when(jpaRepository.existsById(id)).thenReturn(true);

        assertTrue(repository.existsById(id));
        verify(jpaRepository).existsById(id);
    }

    @Test
    @DisplayName("Deve verificar inexistencia por ID")
    void shouldCheckNonExistenceById() {
        when(jpaRepository.existsById(id)).thenReturn(false);

        assertFalse(repository.existsById(id));
    }

    @Test
    @DisplayName("Deve buscar todos os labors paginados")
    void shouldFindAllLaborsPaginated() {
        LaborPageQuery query = mock(LaborPageQuery.class);
        LaborFilter filter = mock(LaborFilter.class);
        when(query.filter()).thenReturn(filter);
        when(query.direction()).thenReturn("ASC");
        when(query.sortBy()).thenReturn("name");
        when(query.page()).thenReturn(0);
        when(query.size()).thenReturn(10);

        Page<LaborJpaEntity> page = new PageImpl<>(List.of(entity));

        try (MockedStatic<LaborSpecificationBuilder> specBuilder = mockStatic(LaborSpecificationBuilder.class);
             MockedStatic<LaborJpaMapper> mapper = mockStatic(LaborJpaMapper.class)) {

            Specification<LaborJpaEntity> spec = mock(Specification.class);
            specBuilder.when(() -> LaborSpecificationBuilder.buildFilterSpecification(filter)).thenReturn(spec);
            when(jpaRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
            mapper.when(() -> LaborJpaMapper.toDomain(entity)).thenReturn(domain);

            LaborPageResult result = repository.findAll(query);

            assertEquals(1, result.items().size());
            assertEquals(1, result.totalElements());
            verify(jpaRepository).findAll(any(Specification.class), any(Pageable.class));
        }
    }

    @Test
    @DisplayName("Deve buscar todos os labors por IDs")
    void shouldFindAllLaborsByIds() {
        Set<UUID> ids = Set.of(id);
        when(jpaRepository.findAllByIds(ids)).thenReturn(List.of(entity));

        try (MockedStatic<LaborJpaMapper> mapper = mockStatic(LaborJpaMapper.class)) {
            mapper.when(() -> LaborJpaMapper.toDomain(entity)).thenReturn(domain);

            List<Labor> result = repository.findAllByIds(ids);

            assertEquals(1, result.size());
            assertSame(domain, result.getFirst());
            verify(jpaRepository).findAllByIds(ids);
        }
    }
}
