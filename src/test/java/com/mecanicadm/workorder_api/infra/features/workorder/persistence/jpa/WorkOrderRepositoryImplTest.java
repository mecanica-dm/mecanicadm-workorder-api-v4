package com.mecanicadm.workorder_api.infra.features.workorder.persistence.jpa;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderBudget;
import com.mecanicadm.workorder_api.core.workorder.domain.enums.WorkOrderStatus;
import com.mecanicadm.workorder_api.core.workorder.domain.port.SortCriteria;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderExecutionDurationProjection;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderExecutionSummaryProjection;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderFilter;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderPageQuery;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderPageResult;
import com.mecanicadm.workorder_api.infra.features.workorder.persistence.entity.WorkOrderBudgetJpaEntity;
import com.mecanicadm.workorder_api.infra.features.workorder.persistence.entity.WorkOrderJpaEntity;
import com.mecanicadm.workorder_api.infra.features.workorder.persistence.jpa.specification.WorkOrderSpecificationBuilder;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkOrderRepositoryImplTest {

    @Mock
    private WorkOrderJpaRepository jpaRepository;
    @Mock
    private WorkOrderLaborItemJpaRepository laborItemJpaRepository;
    @Mock
    private WorkOrderMaterialItemJpaRepository materialItemJpaRepository;
    @Mock
    private WorkOrderBudgetJpaRepository budgetJpaRepository;

    private WorkOrderRepositoryImpl repository;
    private UUID id;
    private WorkOrder domain;
    private WorkOrderJpaEntity entity;

    @BeforeEach
    void setUp() {
        repository = new WorkOrderRepositoryImpl(jpaRepository, laborItemJpaRepository, materialItemJpaRepository, budgetJpaRepository);

        id = UUID.randomUUID();
        domain = mock(WorkOrder.class);
        lenient().when(domain.getId()).thenReturn(id);
        lenient().when(domain.getLaborItems()).thenReturn(Set.of());
        lenient().when(domain.getMaterialItems()).thenReturn(Set.of());
        lenient().when(domain.getBudget()).thenReturn(Optional.empty());

        entity = mock(WorkOrderJpaEntity.class);
        lenient().when(entity.getId()).thenReturn(id);
    }

    @Test
    @DisplayName("Deve criar work order com sucesso")
    void shouldCreateWorkOrderSuccessfully() {
        try (MockedStatic<WorkOrderJpaMapper> mapper = mockStatic(WorkOrderJpaMapper.class)) {

            mapper.when(() -> WorkOrderJpaMapper.toEntity(domain)).thenReturn(entity);
            mapper.when(() -> WorkOrderJpaMapper.toDomainLight(entity)).thenReturn(domain);
            when(jpaRepository.save(entity)).thenReturn(entity);

            WorkOrder result = repository.create(domain);

            assertSame(domain, result);
            verify(jpaRepository).save(entity);
        }
    }

    @Test
    @DisplayName("Deve lancar excecao ao criar work order nula")
    void shouldThrowExceptionWhenCreatingNullWorkOrder() {
        assertThrows(TechnicalException.class, () -> repository.create(null));
        verifyNoInteractions(jpaRepository);
    }

    @Test
    @DisplayName("Deve atualizar work order com sucesso")
    void shouldUpdateWorkOrderSuccessfully() {
        try (MockedStatic<WorkOrderJpaMapper> mapper = mockStatic(WorkOrderJpaMapper.class)) {

            mapper.when(() -> WorkOrderJpaMapper.toEntity(domain)).thenReturn(entity);
            mapper.when(() -> WorkOrderJpaMapper.toDomainLight(entity)).thenReturn(domain);
            when(jpaRepository.save(entity)).thenReturn(entity);

            WorkOrder result = repository.update(domain);

            assertSame(domain, result);
            verify(jpaRepository).save(entity);
        }
    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar work order nula")
    void shouldThrowExceptionWhenUpdatingNullWorkOrder() {
        assertThrows(TechnicalException.class, () -> repository.update(null));
        verifyNoInteractions(jpaRepository);
    }

    @Test
    @DisplayName("Deve buscar work order por ID com sucesso")
    void shouldFindWorkOrderById() {
        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));
        when(laborItemJpaRepository.findByWorkOrderId(id)).thenReturn(List.of());
        when(materialItemJpaRepository.findByWorkOrderId(id)).thenReturn(List.of());
        when(budgetJpaRepository.findById(id)).thenReturn(Optional.empty());

        try (MockedStatic<WorkOrderJpaMapper> mapper = mockStatic(WorkOrderJpaMapper.class)) {
            mapper.when(() -> WorkOrderJpaMapper.toDomain(eq(entity), anySet(), anySet(), any())).thenReturn(domain);

            Optional<WorkOrder> result = repository.findById(id);

            assertTrue(result.isPresent());
            assertSame(domain, result.get());
            verify(jpaRepository).findById(id);
        }
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar work order por ID inexistente")
    void shouldReturnEmptyWhenWorkOrderNotFoundById() {
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        Optional<WorkOrder> result = repository.findById(id);

        assertTrue(result.isEmpty());
        verify(jpaRepository).findById(id);
    }

    @Test
    @DisplayName("Deve buscar work order com items por ID com sucesso")
    void shouldFindWorkOrderByIdWithItems() {
        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));
        when(laborItemJpaRepository.findByWorkOrderId(id)).thenReturn(List.of());
        when(materialItemJpaRepository.findByWorkOrderId(id)).thenReturn(List.of());
        when(budgetJpaRepository.findById(id)).thenReturn(Optional.empty());

        try (MockedStatic<WorkOrderJpaMapper> mapper = mockStatic(WorkOrderJpaMapper.class)) {
            mapper.when(() -> WorkOrderJpaMapper.toDomain(eq(entity), anySet(), anySet(), any())).thenReturn(domain);

            Optional<WorkOrder> result = repository.findByIdWithItems(id);

            assertTrue(result.isPresent());
            assertSame(domain, result.get());
            verify(jpaRepository).findById(id);
        }
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar work order com items por ID inexistente")
    void shouldReturnEmptyWhenWorkOrderWithItemsNotFoundById() {
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        Optional<WorkOrder> result = repository.findByIdWithItems(id);

        assertTrue(result.isEmpty());
        verify(jpaRepository).findById(id);
    }

    @Test
    @DisplayName("Deve buscar todas as work orders paginadas com filtro e sort explícito")
    void shouldFindAllWorkOrdersPaginatedWithExplicitSort() {
        WorkOrderPageQuery query = mock(WorkOrderPageQuery.class);
        WorkOrderFilter filter = mock(WorkOrderFilter.class);
        when(query.filter()).thenReturn(filter);
        when(query.sorts()).thenReturn(List.of(new SortCriteria("executionStartAt", "ASC")));
        when(query.page()).thenReturn(0);
        when(query.size()).thenReturn(10);

        Page<WorkOrderJpaEntity> page = new PageImpl<>(List.of(entity));

        try (MockedStatic<WorkOrderSpecificationBuilder> specBuilder = mockStatic(WorkOrderSpecificationBuilder.class);
             MockedStatic<WorkOrderJpaMapper> mapper = mockStatic(WorkOrderJpaMapper.class)) {

            Specification<WorkOrderJpaEntity> spec = mock(Specification.class);
            specBuilder.when(() -> WorkOrderSpecificationBuilder.buildFilterSpecification(filter)).thenReturn(spec);
            when(jpaRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
            mapper.when(() -> WorkOrderJpaMapper.toDomainLight(entity)).thenReturn(domain);

            WorkOrderPageResult result = repository.findAll(query);

            assertEquals(1, result.items().size());
            assertEquals(1, result.totalElements());
            verify(jpaRepository).findAll(any(Specification.class), any(Pageable.class));
        }
    }

    @Test
    @DisplayName("Deve buscar todas as work orders paginadas com ordenação por status")
    void shouldFindAllWorkOrdersPaginatedWithStatusSort() {
        WorkOrderPageQuery query = mock(WorkOrderPageQuery.class);
        WorkOrderFilter filter = mock(WorkOrderFilter.class);
        when(query.filter()).thenReturn(filter);
        when(query.sorts()).thenReturn(List.of(new SortCriteria("status", "ASC"), new SortCriteria("dateCreated", "ASC")));
        when(query.page()).thenReturn(0);
        when(query.size()).thenReturn(10);

        Page<WorkOrderJpaEntity> page = new PageImpl<>(List.of(entity));

        try (MockedStatic<WorkOrderSpecificationBuilder> specBuilder = mockStatic(WorkOrderSpecificationBuilder.class);
             MockedStatic<WorkOrderJpaMapper> mapper = mockStatic(WorkOrderJpaMapper.class)) {

            Specification<WorkOrderJpaEntity> spec = mock(Specification.class);
            specBuilder.when(() -> WorkOrderSpecificationBuilder.buildFilterSpecification(filter)).thenReturn(spec);
            when(jpaRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
            mapper.when(() -> WorkOrderJpaMapper.toDomainLight(entity)).thenReturn(domain);

            WorkOrderPageResult result = repository.findAll(query);

            assertEquals(1, result.items().size());
            assertEquals(1, result.totalElements());
            verify(jpaRepository).findAll(any(Specification.class), any(Pageable.class));
        }
    }

    @Test
    @DisplayName("Deve somar total de materiais por work order ID")
    void shouldSumMaterialsTotalByWorkOrderId() {
        when(jpaRepository.sumMaterialsTotalByWorkOrderId(id)).thenReturn(new BigDecimal("250.00"));

        BigDecimal result = repository.sumMaterialsTotalByWorkOrderId(id);

        assertEquals(new BigDecimal("250.00"), result);
        verify(jpaRepository).sumMaterialsTotalByWorkOrderId(id);
    }

    @Test
    @DisplayName("Deve somar total de labor por work order ID")
    void shouldSumLaborTotalByWorkOrderId() {
        when(jpaRepository.sumLaborTotalByWorkOrderId(id)).thenReturn(new BigDecimal("150.00"));

        BigDecimal result = repository.sumLaborTotalByWorkOrderId(id);

        assertEquals(new BigDecimal("150.00"), result);
        verify(jpaRepository).sumLaborTotalByWorkOrderId(id);
    }

    @Test
    @DisplayName("Deve obter sumario de tempo de execucao")
    void shouldGetExecutionTimeSummary() {
        var initial = LocalDateTime.now().minusDays(30);
        var finalExclusive = LocalDateTime.now();
        var projection = mock(WorkOrderExecutionSummaryProjection.class);
        when(jpaRepository.getExecutionTimeSummary(initial, finalExclusive)).thenReturn(projection);

        var result = repository.getExecutionTimeSummary(initial, finalExclusive);

        assertSame(projection, result);
        verify(jpaRepository).getExecutionTimeSummary(initial, finalExclusive);
    }

    @Test
    @DisplayName("Deve obter execucao mais lenta")
    void shouldGetSlowestExecution() {
        var initial = LocalDateTime.now().minusDays(30);
        var finalExclusive = LocalDateTime.now();
        var projection = mock(WorkOrderExecutionDurationProjection.class);
        when(jpaRepository.getSlowestExecution(initial, finalExclusive)).thenReturn(projection);

        var result = repository.getSlowestExecution(initial, finalExclusive);

        assertSame(projection, result);
        verify(jpaRepository).getSlowestExecution(initial, finalExclusive);
    }

    @Test
    @DisplayName("Deve obter execucao mais rapida")
    void shouldGetFastestExecution() {
        var initial = LocalDateTime.now().minusDays(30);
        var finalExclusive = LocalDateTime.now();
        var projection = mock(WorkOrderExecutionDurationProjection.class);
        when(jpaRepository.getFastestExecution(initial, finalExclusive)).thenReturn(projection);

        var result = repository.getFastestExecution(initial, finalExclusive);

        assertSame(projection, result);
        verify(jpaRepository).getFastestExecution(initial, finalExclusive);
    }

    @Test
    @DisplayName("Deve obter media de minutos de execucao de labor")
    void shouldGetAverageLaborExecutionMinutes() {
        var initial = LocalDateTime.now().minusDays(30);
        var finalExclusive = LocalDateTime.now();
        when(jpaRepository.getAverageLaborExecutionMinutes(initial, finalExclusive)).thenReturn(45.0);

        var result = repository.getAverageLaborExecutionMinutes(initial, finalExclusive);

        assertEquals(45.0, result);
        verify(jpaRepository).getAverageLaborExecutionMinutes(initial, finalExclusive);
    }

    @Test
    @DisplayName("Deve salvar budget com sucesso")
    void shouldSaveBudgetSuccessfully() {
        WorkOrderBudget budget = mock(WorkOrderBudget.class);
        WorkOrderBudgetJpaEntity budgetEntity = mock(WorkOrderBudgetJpaEntity.class);

        try (MockedStatic<WorkOrderBudgetJpaMapper> mapper = mockStatic(WorkOrderBudgetJpaMapper.class)) {
            mapper.when(() -> WorkOrderBudgetJpaMapper.toEntity(budget)).thenReturn(budgetEntity);

            repository.saveBudget(budget);

            verify(budgetJpaRepository).save(budgetEntity);
        }
    }

    @Test
    @DisplayName("Deve lancar excecao ao salvar budget nulo")
    void shouldThrowExceptionWhenSavingNullBudget() {
        assertThrows(TechnicalException.class, () -> repository.saveBudget(null));
        verifyNoInteractions(budgetJpaRepository);
    }

    @Test
    @DisplayName("Deve verificar existencia por ID")
    void shouldCheckExistsById() {
        when(jpaRepository.existsById(id)).thenReturn(true);

        boolean result = repository.existsById(id);

        assertTrue(result);
        verify(jpaRepository).existsById(id);
    }

    @Test
    @DisplayName("Deve retornar falso quando nao existe por ID")
    void shouldReturnFalseWhenNotExistsById() {
        when(jpaRepository.existsById(id)).thenReturn(false);

        boolean result = repository.existsById(id);

        assertFalse(result);
        verify(jpaRepository).existsById(id);
    }

    @Test
    @DisplayName("Deve buscar status por ID")
    void shouldFindStatusById() {
        when(jpaRepository.findStatusById(id)).thenReturn(Optional.of(WorkOrderStatus.RECEIVED));

        Optional<WorkOrderStatus> result = repository.findStatusById(id);

        assertTrue(result.isPresent());
        assertEquals(WorkOrderStatus.RECEIVED, result.get());
        verify(jpaRepository).findStatusById(id);
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar status por ID inexistente")
    void shouldReturnEmptyWhenFindStatusByIdNotFound() {
        when(jpaRepository.findStatusById(id)).thenReturn(Optional.empty());

        Optional<WorkOrderStatus> result = repository.findStatusById(id);

        assertTrue(result.isEmpty());
        verify(jpaRepository).findStatusById(id);
    }
}
