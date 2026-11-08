package com.mecanicadm.workorder_api.core.vehicle.usecase;

import com.mecanicadm.workorder_api.core.vehicle.domain.Vehicle;
import com.mecanicadm.workorder_api.core.vehicle.domain.port.VehicleGateway;
import com.mecanicadm.workorder_api.core.vehicle.exception.VehicleExceptions;
import com.mecanicadm.workorder_api.core.vehicle.usecase.query.GetVehicleByIdQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetVehicleByIdUseCaseTest {

    private VehicleGateway repository;

    private GetVehicleByIdUseCase useCase;

    @Test
    @DisplayName("Deve retornar um veículo quando a placa existir na base de dados")
    void whenVehicleExists_ShouldReturnVehicle() {
        repository = mock(VehicleGateway.class);
        useCase = new GetVehicleByIdUseCase(repository);
        String licensePlate = "BRA2E19";
        GetVehicleByIdQuery query = new GetVehicleByIdQuery(licensePlate);
        Vehicle expectedVehicle = mock(Vehicle.class);

        when(repository.findByLicensePlate(licensePlate)).thenReturn(Optional.of(expectedVehicle));

        Vehicle result = useCase.execute(query);

        assertNotNull(result);
        assertEquals(expectedVehicle, result);
        verify(repository, times(1)).findByLicensePlate(licensePlate);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando o veículo não for encontrado")
    void whenVehicleDoesNotExist_ShouldThrowNotFoundException() {
        repository = mock(VehicleGateway.class);
        useCase = new GetVehicleByIdUseCase(repository);
        String licensePlate = "NOTFOUND";
        GetVehicleByIdQuery query = new GetVehicleByIdQuery(licensePlate);

        when(repository.findByLicensePlate(licensePlate)).thenReturn(Optional.empty());

        assertThrows(VehicleExceptions.NotFound.class, () -> useCase.execute(query));
        verify(repository, times(1)).findByLicensePlate(licensePlate);
    }
}