package com.mecanicadm.workorder_api.core.vehicle.usecase;

import com.mecanicadm.workorder_api.core.vehicle.domain.Vehicle;
import com.mecanicadm.workorder_api.core.vehicle.domain.port.VehicleGateway;
import com.mecanicadm.workorder_api.core.vehicle.domain.port.VehiclePageQuery;
import com.mecanicadm.workorder_api.core.vehicle.domain.port.VehiclePageResult;
import com.mecanicadm.workorder_api.core.vehicle.usecase.query.GetAllVehiclesQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllVehicleUseCaseTest {

    private VehicleGateway repository;

    @Test
    @DisplayName("Deve retornar página de veículos sem filtros quando a query estiver vazia")
    void shouldReturnVehiclesPageWhenNoFiltersProvided() {
        repository = mock(VehicleGateway.class);
        GetAllVehiclesQuery query = new GetAllVehiclesQuery(null, null, null, null, 0, 10, "licensePlate", "ASC");
        VehiclePageResult expectedPage = new VehiclePageResult(List.of(mock(Vehicle.class)), 1);
        GetAllVehicleUseCase useCase = new GetAllVehicleUseCase(repository);

        when(repository.findAll(argThat(q -> q.page() == 0 && q.size() == 10 && q.sortBy().equals("licensePlate") && q.direction().equals("ASC")))).thenReturn(expectedPage);

        VehiclePageResult result = useCase.execute(query);

        assertNotNull(result);
        assertEquals(1, result.items().size());
        verify(repository).findAll(any(VehiclePageQuery.class));
    }

    @Test
    @DisplayName("Deve aplicar filtro de placa quando fornecido na query")
    void shouldApplyLicensePlateFilter() {
        repository = mock(VehicleGateway.class);
        GetAllVehiclesQuery query = new GetAllVehiclesQuery("ABC-1234", null, null, null, 0, 10, "licensePlate", "ASC");
        VehiclePageResult expectedPage = new VehiclePageResult(List.of(mock(Vehicle.class)), 1);
        GetAllVehicleUseCase useCase = new GetAllVehicleUseCase(repository);

        when(repository.findAll(any(VehiclePageQuery.class))).thenReturn(expectedPage);

        VehiclePageResult result = useCase.execute(query);

        assertNotNull(result);
        verify(repository).findAll(argThat(q -> "ABC-1234".equals(q.filter().licensePlate())));
    }

    @Test
    @DisplayName("Deve aplicar filtro de modelo quando fornecido na query")
    void shouldApplyModelFilter() {
        repository = mock(VehicleGateway.class);
        GetAllVehiclesQuery query = new GetAllVehiclesQuery(null, "Civic", null, null, 0, 10, "licensePlate", "ASC");
        VehiclePageResult expectedPage = new VehiclePageResult(List.of(mock(Vehicle.class)), 1);
        GetAllVehicleUseCase useCase = new GetAllVehicleUseCase(repository);

        when(repository.findAll(any(VehiclePageQuery.class))).thenReturn(expectedPage);

        VehiclePageResult result = useCase.execute(query);

        assertNotNull(result);
        verify(repository).findAll(argThat(q -> "Civic".equals(q.filter().model())));
    }
}