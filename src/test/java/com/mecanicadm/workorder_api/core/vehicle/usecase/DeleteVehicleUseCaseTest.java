package com.mecanicadm.workorder_api.core.vehicle.usecase;

import com.mecanicadm.workorder_api.core.vehicle.domain.Vehicle;
import com.mecanicadm.workorder_api.core.vehicle.domain.port.VehicleGateway;
import com.mecanicadm.workorder_api.core.vehicle.exception.VehicleExceptions;
import com.mecanicadm.workorder_api.core.vehicle.usecase.command.DeleteVehicleCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteVehicleUseCaseTest {

    private VehicleGateway repository;

    private DeleteVehicleUseCase deleteVehicleUseCase;

    private DeleteVehicleCommand command;
    private String licensePlate;

    @BeforeEach
    void setUp() {
        repository = mock(VehicleGateway.class);
        deleteVehicleUseCase = new DeleteVehicleUseCase(repository);
        licensePlate = "ABC-1234";
        command = new DeleteVehicleCommand(licensePlate);
    }

    @Test
    @DisplayName("Should soft delete vehicle when it exists")
    void shouldDeleteVehicleWhenItExists() {
        Vehicle vehicle = Vehicle.restore("Civic", licensePlate, "Honda", (short) 2023, null, null, null);
        when(repository.findByLicensePlate(licensePlate)).thenReturn(Optional.of(vehicle));

        deleteVehicleUseCase.execute(command);

        verify(repository, times(1)).findByLicensePlate(licensePlate);
        verify(repository, times(1)).update(vehicle);
    }

    @Test
    @DisplayName("Should throw NotFound exception when vehicle does not exist")
    void shouldThrowNotFoundExceptionWhenVehicleDoesNotExist() {
        when(repository.findByLicensePlate(licensePlate)).thenReturn(Optional.empty());

        assertThrows(VehicleExceptions.NotFound.class, () -> deleteVehicleUseCase.execute(command));

        verify(repository, times(1)).findByLicensePlate(licensePlate);
        verify(repository, never()).update(any());
    }
}
