package com.mecanicadm.workorder_api.infra.features.vehicle.persistence.jpa;

import com.mecanicadm.workorder_api.core.vehicle.domain.Vehicle;
import com.mecanicadm.workorder_api.core.vehicle.domain.port.VehicleFilter;
import com.mecanicadm.workorder_api.core.vehicle.domain.port.VehiclePageQuery;
import com.mecanicadm.workorder_api.core.vehicle.domain.port.VehiclePageResult;
import com.mecanicadm.workorder_api.infra.features.vehicle.persistence.entity.VehicleJpaEntity;
import com.mecanicadm.workorder_api.infra.features.vehicle.persistence.jpa.specification.VehicleSpecificationBuilder;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleRepositoryImplTest {

    @Mock
    private VehicleJpaRepository jpaRepository;

    private VehicleRepositoryImpl repository;
    private String licensePlate;
    private Vehicle domain;
    private VehicleJpaEntity entity;

    @BeforeEach
    void setUp() {
        repository = new VehicleRepositoryImpl(jpaRepository);

        licensePlate = "ABC1234";
        domain = mock(Vehicle.class);

        entity = new VehicleJpaEntity(licensePlate, "Civic", "Honda", (short) 2023);
    }

    @Test
    @DisplayName("Deve criar veiculo com sucesso")
    void shouldCreateVehicleSuccessfully() {
        try (MockedStatic<VehicleJpaMapper> mapper = mockStatic(VehicleJpaMapper.class)) {
            mapper.when(() -> VehicleJpaMapper.toEntity(domain)).thenReturn(entity);
            mapper.when(() -> VehicleJpaMapper.toDomain(entity)).thenReturn(domain);
            when(jpaRepository.save(entity)).thenReturn(entity);

            Vehicle result = repository.create(domain);

            assertSame(domain, result);
            verify(jpaRepository).save(entity);
        }
    }

    @Test
    @DisplayName("Deve lancar excecao ao criar veiculo nulo")
    void shouldThrowExceptionWhenCreatingNullVehicle() {
        assertThrows(TechnicalException.class, () -> repository.create(null));
        verifyNoInteractions(jpaRepository);
    }

    @Test
    @DisplayName("Deve atualizar veiculo com sucesso")
    void shouldUpdateVehicleSuccessfully() {
        try (MockedStatic<VehicleJpaMapper> mapper = mockStatic(VehicleJpaMapper.class)) {
            mapper.when(() -> VehicleJpaMapper.toEntity(domain)).thenReturn(entity);
            mapper.when(() -> VehicleJpaMapper.toDomain(entity)).thenReturn(domain);
            when(jpaRepository.save(entity)).thenReturn(entity);

            Vehicle result = repository.update(domain);

            assertSame(domain, result);
            verify(jpaRepository).save(entity);
        }
    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar veiculo nulo")
    void shouldThrowExceptionWhenUpdatingNullVehicle() {
        assertThrows(TechnicalException.class, () -> repository.update(null));
        verifyNoInteractions(jpaRepository);
    }

    @Test
    @DisplayName("Deve buscar veiculo pela placa com sucesso")
    void shouldFindByLicensePlate() {
        when(jpaRepository.findByLicensePlate(licensePlate)).thenReturn(Optional.of(entity));

        try (MockedStatic<VehicleJpaMapper> mapper = mockStatic(VehicleJpaMapper.class)) {
            mapper.when(() -> VehicleJpaMapper.toDomain(entity)).thenReturn(domain);

            Optional<Vehicle> result = repository.findByLicensePlate(licensePlate);

            assertTrue(result.isPresent());
            assertSame(domain, result.get());
            verify(jpaRepository).findByLicensePlate(licensePlate);
        }
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar veiculo pela placa inexistente")
    void shouldReturnEmptyWhenNotFoundByLicensePlate() {
        String plate = "ABC1234";
        when(jpaRepository.findByLicensePlate(plate)).thenReturn(Optional.empty());

        Optional<Vehicle> result = repository.findByLicensePlate(plate);

        assertTrue(result.isEmpty());
        verify(jpaRepository).findByLicensePlate(plate);
    }

    @Test
    @DisplayName("Deve verificar existencia de placa")
    void shouldCheckLicensePlateExistence() {
        when(jpaRepository.existsByLicensePlate(licensePlate)).thenReturn(true);

        assertTrue(repository.existsByLicensePlate(licensePlate));
        verify(jpaRepository).existsByLicensePlate(licensePlate);
    }

    @Test
    @DisplayName("Deve verificar inexistencia de placa")
    void shouldCheckLicensePlateNonExistence() {
        when(jpaRepository.existsByLicensePlate(licensePlate)).thenReturn(false);

        assertFalse(repository.existsByLicensePlate(licensePlate));
    }

    @Test
    @DisplayName("Deve buscar todos os veiculos paginados")
    void shouldFindAllVehiclesPaginated() {
        VehiclePageQuery query = mock(VehiclePageQuery.class);
        VehicleFilter filter = mock(VehicleFilter.class);
        when(query.filter()).thenReturn(filter);
        when(query.direction()).thenReturn("ASC");
        when(query.sortBy()).thenReturn("licensePlate");
        when(query.page()).thenReturn(0);
        when(query.size()).thenReturn(10);

        Page<VehicleJpaEntity> page = new PageImpl<>(List.of(entity));

        try (MockedStatic<VehicleSpecificationBuilder> specBuilder = mockStatic(VehicleSpecificationBuilder.class);
             MockedStatic<VehicleJpaMapper> mapper = mockStatic(VehicleJpaMapper.class)) {

            Specification<VehicleJpaEntity> spec = mock(Specification.class);
            specBuilder.when(() -> VehicleSpecificationBuilder.buildFilterSpecification(filter)).thenReturn(spec);
            when(jpaRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
            mapper.when(() -> VehicleJpaMapper.toDomain(entity)).thenReturn(domain);

            VehiclePageResult result = repository.findAll(query);

            assertEquals(1, result.items().size());
            assertEquals(1, result.totalElements());
            verify(jpaRepository).findAll(any(Specification.class), any(Pageable.class));
        }
    }
}
