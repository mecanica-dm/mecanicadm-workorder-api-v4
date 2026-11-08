package com.mecanicadm.workorder_api.infra.features.material.persistence.jpa;

import com.mecanicadm.workorder_api.core.material.domain.Material;
import com.mecanicadm.workorder_api.core.material.domain.port.MaterialFilter;
import com.mecanicadm.workorder_api.core.material.domain.port.MaterialPageQuery;
import com.mecanicadm.workorder_api.core.material.domain.port.MaterialPageResult;
import com.mecanicadm.workorder_api.infra.features.material.persistence.entity.MaterialJpaEntity;
import com.mecanicadm.workorder_api.infra.features.material.persistence.jpa.specification.MaterialSpecificationBuilder;
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

import com.mecanicadm.workorder_api.core.material.domain.enums.MaterialType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaterialRepositoryImplTest {

    @Mock
    private MaterialJpaRepository jpaRepository;

    private MaterialRepositoryImpl repository;
    private UUID id;
    private Material domain;
    private MaterialJpaEntity entity;

    @BeforeEach
    void setUp() {
        repository = new MaterialRepositoryImpl(jpaRepository);

        id = UUID.randomUUID();
        domain = mock(Material.class);
        lenient().when(domain.getId()).thenReturn(id);

        entity = new MaterialJpaEntity(id, "Material Test", "Marca", "Descricao", BigDecimal.TEN, MaterialType.PART);
    }

    @Test
    @DisplayName("Deve criar material com sucesso")
    void shouldCreateMaterialSuccessfully() {
        try (MockedStatic<MaterialJpaMapper> mapper = mockStatic(MaterialJpaMapper.class)) {
            mapper.when(() -> MaterialJpaMapper.toEntity(domain)).thenReturn(entity);
            mapper.when(() -> MaterialJpaMapper.toDomain(entity)).thenReturn(domain);
            when(jpaRepository.save(entity)).thenReturn(entity);

            Material result = repository.create(domain);

            assertSame(domain, result);
            verify(jpaRepository).save(entity);
        }
    }

    @Test
    @DisplayName("Deve lancar excecao ao criar material nulo")
    void shouldThrowExceptionWhenCreatingNullMaterial() {
        assertThrows(TechnicalException.class, () -> repository.create(null));
        verifyNoInteractions(jpaRepository);
    }

    @Test
    @DisplayName("Deve atualizar material com sucesso")
    void shouldUpdateMaterialSuccessfully() {
        try (MockedStatic<MaterialJpaMapper> mapper = mockStatic(MaterialJpaMapper.class)) {
            mapper.when(() -> MaterialJpaMapper.toEntity(domain)).thenReturn(entity);
            mapper.when(() -> MaterialJpaMapper.toDomain(entity)).thenReturn(domain);
            when(jpaRepository.save(entity)).thenReturn(entity);

            Material result = repository.update(domain);

            assertSame(domain, result);
            verify(jpaRepository).save(entity);
        }
    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar material nulo")
    void shouldThrowExceptionWhenUpdatingNullMaterial() {
        assertThrows(TechnicalException.class, () -> repository.update(null));
        verifyNoInteractions(jpaRepository);
    }

    @Test
    @DisplayName("Deve buscar material por ID com sucesso")
    void shouldFindMaterialById() {
        when(jpaRepository.findActiveById(id)).thenReturn(Optional.of(entity));

        try (MockedStatic<MaterialJpaMapper> mapper = mockStatic(MaterialJpaMapper.class)) {
            mapper.when(() -> MaterialJpaMapper.toDomain(entity)).thenReturn(domain);

            Optional<Material> result = repository.findById(id);

            assertTrue(result.isPresent());
            assertSame(domain, result.get());
            verify(jpaRepository).findActiveById(id);
        }
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar material por ID inexistente")
    void shouldReturnEmptyWhenMaterialNotFoundById() {
        when(jpaRepository.findActiveById(id)).thenReturn(Optional.empty());

        Optional<Material> result = repository.findById(id);

        assertTrue(result.isEmpty());
        verify(jpaRepository).findActiveById(id);
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
    @DisplayName("Deve buscar todos os materiais paginados")
    void shouldFindAllMaterialsPaginated() {
        MaterialPageQuery query = mock(MaterialPageQuery.class);
        MaterialFilter filter = mock(MaterialFilter.class);
        when(query.filter()).thenReturn(filter);
        when(query.direction()).thenReturn("ASC");
        when(query.sortBy()).thenReturn("name");
        when(query.page()).thenReturn(0);
        when(query.size()).thenReturn(10);

        Page<MaterialJpaEntity> page = new PageImpl<>(List.of(entity));

        try (MockedStatic<MaterialSpecificationBuilder> specBuilder = mockStatic(MaterialSpecificationBuilder.class);
             MockedStatic<MaterialJpaMapper> mapper = mockStatic(MaterialJpaMapper.class)) {

            Specification<MaterialJpaEntity> spec = mock(Specification.class);
            specBuilder.when(() -> MaterialSpecificationBuilder.buildFilterSpecification(filter)).thenReturn(spec);
            when(jpaRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
            mapper.when(() -> MaterialJpaMapper.toDomain(entity)).thenReturn(domain);

            MaterialPageResult result = repository.findAll(query);

            assertEquals(1, result.items().size());
            assertEquals(1, result.totalElements());
            verify(jpaRepository).findAll(any(Specification.class), any(Pageable.class));
        }
    }

    @Test
    @DisplayName("Deve buscar todos os materiais por IDs")
    void shouldFindAllMaterialsByIds() {
        Set<UUID> ids = Set.of(id);
        when(jpaRepository.findAllByIds(ids)).thenReturn(List.of(entity));

        try (MockedStatic<MaterialJpaMapper> mapper = mockStatic(MaterialJpaMapper.class)) {
            mapper.when(() -> MaterialJpaMapper.toDomain(entity)).thenReturn(domain);

            List<Material> result = repository.findAllByIds(ids);

            assertEquals(1, result.size());
            assertSame(domain, result.getFirst());
            verify(jpaRepository).findAllByIds(ids);
        }
    }
}
