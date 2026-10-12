package com.mecanicadm.workorder_api.infra.features.workorder.persistence.jpa;

import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderLaborItem;
import com.mecanicadm.workorder_api.infra.features.workorder.persistence.entity.WorkOrderLaborItemJpaEntity;
import com.mecanicadm.workorder_api.shared.exception.TechnicalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkOrderLaborItemRepositoryImplTest {

    @Mock
    private WorkOrderLaborItemJpaRepository jpaRepository;

    private WorkOrderLaborItemRepositoryImpl repository;
    private UUID id;
    private WorkOrderLaborItem laborItem;
    private WorkOrderLaborItemJpaEntity entity;

    private UUID workOrderId;

    @BeforeEach
    void setUp() {
        repository = new WorkOrderLaborItemRepositoryImpl(jpaRepository);

        id = UUID.randomUUID();
        workOrderId = UUID.randomUUID();
        laborItem = mock(WorkOrderLaborItem.class);
        lenient().when(laborItem.getId()).thenReturn(id);
        lenient().when(laborItem.getWorkOrderId()).thenReturn(workOrderId);

        entity = mock(WorkOrderLaborItemJpaEntity.class);
    }

    @Test
    @DisplayName("Deve buscar labor item por ID com sucesso")
    void shouldFindLaborItemById() {
        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));

        try (MockedStatic<WorkOrderLaborItemJpaMapper> mapper = mockStatic(WorkOrderLaborItemJpaMapper.class)) {
            mapper.when(() -> WorkOrderLaborItemJpaMapper.toDomain(entity)).thenReturn(laborItem);

            Optional<WorkOrderLaborItem> result = repository.findById(id);

            assertTrue(result.isPresent());
            assertSame(laborItem, result.get());
            verify(jpaRepository).findById(id);
        }
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar labor item por ID inexistente")
    void shouldReturnEmptyWhenLaborItemNotFoundById() {
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        Optional<WorkOrderLaborItem> result = repository.findById(id);

        assertTrue(result.isEmpty());
        verify(jpaRepository).findById(id);
    }

    @Test
    @DisplayName("Deve criar labor item com sucesso")
    void shouldCreateLaborItemSuccessfully() {
        when(jpaRepository.save(entity)).thenReturn(entity);

        try (MockedStatic<WorkOrderLaborItemJpaMapper> mapper = mockStatic(WorkOrderLaborItemJpaMapper.class)) {
            mapper.when(() -> WorkOrderLaborItemJpaMapper.toEntity(laborItem)).thenReturn(entity);
            mapper.when(() -> WorkOrderLaborItemJpaMapper.toDomain(entity)).thenReturn(laborItem);

            WorkOrderLaborItem result = repository.create(laborItem);

            assertSame(laborItem, result);
            verify(jpaRepository).save(entity);
        }
    }

    @Test
    @DisplayName("Deve lancar excecao ao criar labor item nulo")
    void shouldThrowExceptionWhenCreatingNullLaborItem() {
        assertThrows(TechnicalException.class, () -> repository.create(null));
        verifyNoInteractions(jpaRepository);
    }

    @Test
    @DisplayName("Deve atualizar labor item com sucesso")
    void shouldUpdateLaborItemSuccessfully() {
        when(jpaRepository.save(entity)).thenReturn(entity);

        try (MockedStatic<WorkOrderLaborItemJpaMapper> mapper = mockStatic(WorkOrderLaborItemJpaMapper.class)) {
            mapper.when(() -> WorkOrderLaborItemJpaMapper.toEntity(laborItem)).thenReturn(entity);
            mapper.when(() -> WorkOrderLaborItemJpaMapper.toDomain(entity)).thenReturn(laborItem);

            WorkOrderLaborItem result = repository.update(laborItem);

            assertSame(laborItem, result);
            verify(jpaRepository).save(entity);
        }
    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar labor item nulo")
    void shouldThrowExceptionWhenUpdatingNullLaborItem() {
        assertThrows(TechnicalException.class, () -> repository.update(null));
        verifyNoInteractions(jpaRepository);
    }

    @Test
    @DisplayName("Deve deletar labor item com sucesso")
    void shouldDeleteLaborItemSuccessfully() {
        repository.delete(laborItem);

        verify(jpaRepository).deleteById(id);
    }

    @Test
    @DisplayName("Deve lancar excecao ao deletar labor item nulo")
    void shouldThrowExceptionWhenDeletingNullLaborItem() {
        assertThrows(TechnicalException.class, () -> repository.delete(null));
        verifyNoInteractions(jpaRepository);
    }
}
