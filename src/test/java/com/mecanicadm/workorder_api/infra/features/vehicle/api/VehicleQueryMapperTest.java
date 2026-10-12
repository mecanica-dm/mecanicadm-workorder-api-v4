package com.mecanicadm.workorder_api.infra.features.vehicle.api;

import com.mecanicadm.workorder_api.core.vehicle.domain.Vehicle;
import com.mecanicadm.workorder_api.core.vehicle.domain.port.VehiclePageResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VehicleQueryMapperTest {

    @Test
    @DisplayName("Deve converter parâmetros de consulta para GetAllVehiclesQuery")
    void shouldConvertToQuery() {
        var pageable = PageRequest.of(0, 20, Sort.by("licensePlate").ascending());

        var query = VehicleQueryMapper.toQuery("ABC1234", "Civic", "Honda", (short) 2023, pageable);

        assertEquals("ABC1234", query.licensePlate());
        assertEquals("Civic", query.model());
        assertEquals("Honda", query.brand());
        assertEquals(Short.valueOf((short) 2023), query.modelYear());
        assertEquals(0, query.page());
        assertEquals(20, query.size());
        assertEquals("licensePlate", query.sortBy());
        assertEquals("ASC", query.direction());
    }

    @Test
    @DisplayName("Deve usar valores padrão para sort quando não especificado")
    void shouldUseDefaultSortWhenNotProvided() {
        var pageable = PageRequest.of(1, 10);

        var query = VehicleQueryMapper.toQuery(null, null, null, null, pageable);

        assertEquals(1, query.page());
        assertEquals(10, query.size());
        assertEquals("licensePlate", query.sortBy());
        assertEquals("ASC", query.direction());
    }

    @Test
    @DisplayName("Deve converter VehiclePageResult para Page de VehicleResponse")
    void shouldConvertToPage() {
        var vehicle = Vehicle.create("Civic", "ABC1234", "Honda", (short) 2023);
        var result = new VehiclePageResult(List.of(vehicle), 1L);
        var pageable = PageRequest.of(0, 20);

        var page = VehicleQueryMapper.toPage(result, pageable);

        assertEquals(1, page.getContent().size());
        assertEquals(1, page.getTotalElements());
        assertEquals("ABC1234", page.getContent().getFirst().licensePlate());
    }
}
