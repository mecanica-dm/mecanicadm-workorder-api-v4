package com.mecanicadm.workorder_api.core.vehicle.usecase;

import com.mecanicadm.workorder_api.core.vehicle.domain.Vehicle;
import com.mecanicadm.workorder_api.core.vehicle.domain.port.VehicleGateway;
import com.mecanicadm.workorder_api.core.vehicle.exception.VehicleExceptions;
import com.mecanicadm.workorder_api.core.vehicle.usecase.command.CreateVehicleCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateVehicleUseCaseTest {

    private VehicleGateway repository;

    private CreateVehicleUseCase createVehicleUseCase;

    private CreateVehicleCommand command;

    @BeforeEach
    void setUp() {
        repository = mock(VehicleGateway.class);
        createVehicleUseCase = new CreateVehicleUseCase(repository);
        command = new CreateVehicleCommand(
                "Civic",
                "ABC1234",
                "Honda",
                Short.valueOf("2023")
        );
    }

    @Test
    @DisplayName("Deve criar um veículo com sucesso quando a placa não existe")
    void shouldCreateVehicleSuccessfully() {
        when(repository.existsByLicensePlate(command.licensePlate())).thenReturn(false);

        Vehicle savedVehicle = Vehicle.create(command.model(), command.licensePlate(), command.brand(), command.modelYear());
        when(repository.create(any(Vehicle.class))).thenReturn(savedVehicle);

        String resultLicensePlate = createVehicleUseCase.execute(command);

        assertNotNull(resultLicensePlate);
        assertEquals(command.licensePlate(), resultLicensePlate);
        verify(repository).existsByLicensePlate(command.licensePlate());
        verify(repository).create(any(Vehicle.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando a placa já está cadastrada")
    void shouldThrowExceptionWhenVehicleExists() {
        when(repository.existsByLicensePlate(command.licensePlate())).thenReturn(true);

        assertThrows(VehicleExceptions.VehicleExists.class, () -> createVehicleUseCase.execute(command));

        verify(repository).existsByLicensePlate(command.licensePlate());
        verify(repository, never()).create(any(Vehicle.class));
    }
}
