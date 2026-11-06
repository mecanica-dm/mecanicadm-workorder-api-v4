package com.mecanicadm.workorder_api.infra.features.stockmovements.persistence.jpa;

import com.mecanicadm.workorder_api.core.stockmovements.domain.StockMovements;
import com.mecanicadm.workorder_api.core.stockmovements.domain.enums.MovementType;
import com.mecanicadm.workorder_api.core.stockmovements.domain.port.StockMovementsFilter;
import com.mecanicadm.workorder_api.infra.features.stockmovements.persistence.entity.StockMovementsJpaEntity;
import com.mecanicadm.workorder_api.shared.exception.TechnicalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockMovementsRepositoryImplTest {

    @Mock
    private StockMovementsJpaRepository jpaRepository;

    private StockMovementsRespositoryImpl repository;
    private UUID id;
    private StockMovements domain;
    private StockMovementsJpaEntity entity;

    @BeforeEach
    void setUp() {
        repository = new StockMovementsRespositoryImpl(jpaRepository);

        id = UUID.randomUUID();
        domain = mock(StockMovements.class);
        lenient().when(domain.getId()).thenReturn(id);

        entity = new StockMovementsJpaEntity(id, UUID.randomUUID(), UUID.randomUUID(), 5, MovementType.ADDITION);
    }

    @Test
    @DisplayName("Deve criar stock movement com sucesso")
    void shouldCreateSuccessfully() {
        try (MockedStatic<StockMovementsJpaMapper> mapper = mockStatic(StockMovementsJpaMapper.class)) {
            mapper.when(() -> StockMovementsJpaMapper.toEntity(domain)).thenReturn(entity);
            mapper.when(() -> StockMovementsJpaMapper.toDomain(entity)).thenReturn(domain);
            when(jpaRepository.save(entity)).thenReturn(entity);

            StockMovements result = repository.create(domain);

            assertSame(domain, result);
            verify(jpaRepository).save(entity);
        }
    }

    @Test
    @DisplayName("Deve lancar excecao ao criar stock movement nulo")
    void shouldThrowExceptionWhenCreatingNull() {
        assertThrows(TechnicalException.class, () -> repository.create(null));
        verifyNoInteractions(jpaRepository);
    }

    @Test
    @DisplayName("Deve atualizar stock movement com sucesso")
    void shouldUpdateSuccessfully() {
        try (MockedStatic<StockMovementsJpaMapper> mapper = mockStatic(StockMovementsJpaMapper.class)) {
            mapper.when(() -> StockMovementsJpaMapper.toEntity(domain)).thenReturn(entity);
            mapper.when(() -> StockMovementsJpaMapper.toDomain(entity)).thenReturn(domain);
            when(jpaRepository.save(entity)).thenReturn(entity);

            StockMovements result = repository.update(domain);

            assertSame(domain, result);
            verify(jpaRepository).save(entity);
        }
    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar stock movement nulo")
    void shouldThrowExceptionWhenUpdatingNull() {
        assertThrows(TechnicalException.class, () -> repository.update(null));
        verifyNoInteractions(jpaRepository);
    }

    @Test
    @DisplayName("Deve buscar todos por material ID ordenado por data")
    void shouldFindAllByMaterialIdOrderByDateCreatedDesc() {
        UUID materialId = UUID.randomUUID();
        StockMovementsFilter filter = mock(StockMovementsFilter.class);
        when(filter.materialId()).thenReturn(materialId);
        when(jpaRepository.findAllByMaterialIdOrderByDateCreatedDesc(materialId)).thenReturn(List.of(entity));

        try (MockedStatic<StockMovementsJpaMapper> mapper = mockStatic(StockMovementsJpaMapper.class)) {
            mapper.when(() -> StockMovementsJpaMapper.toDomain(entity)).thenReturn(domain);

            List<StockMovements> result = repository.findAllByMaterialIdOrderByDateCreatedDesc(filter);

            assertEquals(1, result.size());
            assertSame(domain, result.getFirst());
            verify(jpaRepository).findAllByMaterialIdOrderByDateCreatedDesc(materialId);
        }
    }

    @Test
    @DisplayName("Deve buscar saldo atual por material ID com valor presente")
    void shouldGetCurrentBalanceWhenValueExists() {
        UUID materialId = UUID.randomUUID();
        when(jpaRepository.getCurrentBalanceByMaterialId(materialId)).thenReturn(10);

        int balance = repository.getCurrentBalanceByMaterialId(materialId);

        assertEquals(10, balance);
        verify(jpaRepository).getCurrentBalanceByMaterialId(materialId);
    }

    @Test
    @DisplayName("Deve retornar 0 ao buscar saldo atual por material ID com valor nulo")
    void shouldReturnZeroWhenCurrentBalanceIsNull() {
        UUID materialId = UUID.randomUUID();
        when(jpaRepository.getCurrentBalanceByMaterialId(materialId)).thenReturn(null);

        int balance = repository.getCurrentBalanceByMaterialId(materialId);

        assertEquals(0, balance);
        verify(jpaRepository).getCurrentBalanceByMaterialId(materialId);
    }

    @Test
    @DisplayName("Deve buscar por material ID e work order ID com sucesso")
    void shouldFindByMaterialIdAndWorkOrderId() {
        UUID materialId = UUID.randomUUID();
        UUID workOrderId = UUID.randomUUID();
        when(jpaRepository.findByMaterialIdAndWorkOrderId(materialId, workOrderId)).thenReturn(Optional.of(entity));

        try (MockedStatic<StockMovementsJpaMapper> mapper = mockStatic(StockMovementsJpaMapper.class)) {
            mapper.when(() -> StockMovementsJpaMapper.toDomain(entity)).thenReturn(domain);

            Optional<StockMovements> result = repository.findByMaterialIdAndWorkOrderId(materialId, workOrderId);

            assertTrue(result.isPresent());
            assertSame(domain, result.get());
            verify(jpaRepository).findByMaterialIdAndWorkOrderId(materialId, workOrderId);
        }
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar por material ID e work order ID inexistente")
    void shouldReturnEmptyWhenNotFoundByMaterialIdAndWorkOrderId() {
        UUID materialId = UUID.randomUUID();
        UUID workOrderId = UUID.randomUUID();
        when(jpaRepository.findByMaterialIdAndWorkOrderId(materialId, workOrderId)).thenReturn(Optional.empty());

        Optional<StockMovements> result = repository.findByMaterialIdAndWorkOrderId(materialId, workOrderId);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Deve buscar por material ID com sucesso")
    void shouldFindByMaterialId() {
        UUID materialId = UUID.randomUUID();
        when(jpaRepository.findByMaterialId(materialId)).thenReturn(Optional.of(entity));

        try (MockedStatic<StockMovementsJpaMapper> mapper = mockStatic(StockMovementsJpaMapper.class)) {
            mapper.when(() -> StockMovementsJpaMapper.toDomain(entity)).thenReturn(domain);

            Optional<StockMovements> result = repository.findByMaterialId(materialId);

            assertTrue(result.isPresent());
            assertSame(domain, result.get());
            verify(jpaRepository).findByMaterialId(materialId);
        }
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar por material ID inexistente")
    void shouldReturnEmptyWhenNotFoundByMaterialId() {
        UUID materialId = UUID.randomUUID();
        when(jpaRepository.findByMaterialId(materialId)).thenReturn(Optional.empty());

        Optional<StockMovements> result = repository.findByMaterialId(materialId);

        assertTrue(result.isEmpty());
    }
}
