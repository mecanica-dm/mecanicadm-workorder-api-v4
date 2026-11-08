package com.mecanicadm.workorder_api.core.vehicle.usecase;

import com.mecanicadm.workorder_api.core.vehicle.domain.Vehicle;
import com.mecanicadm.workorder_api.core.vehicle.domain.port.VehicleGateway;
import com.mecanicadm.workorder_api.core.vehicle.exception.VehicleExceptions;
import com.mecanicadm.workorder_api.core.vehicle.usecase.command.UpdateVehicleCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateVehicleUseCaseTest {

    @Mock
    private VehicleGateway repository;

    private UpdateVehicleUseCase updateVehicleUseCase;

    @Test
    @DisplayName("Deve atualizar o modelo do veículo com sucesso")
    void shouldUpdateVehicleSuccessfully() {
        updateVehicleUseCase = new UpdateVehicleUseCase(repository);
        String licensePlate = "ABC-1234";
        String newModel = "Novo Modelo";
        String newBrand = "Nova Marca";
        Short newYear = Short.valueOf("2024");
        UpdateVehicleCommand command = new UpdateVehicleCommand(licensePlate, newModel, newBrand, newYear);

        Vehicle existing = Vehicle.restore("OldModel", licensePlate, "OldBrand", (short) 2015, null, null, null);
        Vehicle updated = existing.update(newModel, newBrand, newYear);

        when(repository.findByLicensePlate(licensePlate)).thenReturn(Optional.of(existing));
        when(repository.update(any(Vehicle.class))).thenReturn(updated);

        Vehicle result = updateVehicleUseCase.execute(command);

        assertEquals(licensePlate, result.getLicensePlate());
        assertEquals(newModel, result.getModel());
        verify(repository).update(any(Vehicle.class));
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar atualizar veículo inexistente")
    void shouldThrowErrorUpdateVehicleNotFound() {
        updateVehicleUseCase = new UpdateVehicleUseCase(repository);
        String licensePlate = "XYZ-9999";
        UpdateVehicleCommand command = new UpdateVehicleCommand(licensePlate, "Polo", "VW", Short.valueOf("2025"));

        when(repository.findByLicensePlate(licensePlate)).thenReturn(Optional.empty());

        assertThrows(VehicleExceptions.NotFound.class, () -> updateVehicleUseCase.execute(command));
        verify(repository, never()).update(any());
    }
}
