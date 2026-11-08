package com.mecanicadm.workorder_api.core.vehicle.domain;

import com.mecanicadm.workorder_api.core.vehicle.exception.VehicleExceptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class VehicleTest {

    @Test
    @DisplayName("Deve instanciar um veículo corretamente")
    void shouldInstantiateVehicle() {
        String model = "City Hatch";
        String licensePlate = "ABC1234";
        String brand = "Honda";
        Short modelYear = 2022;

        Vehicle vehicle = Vehicle.create(model, licensePlate, brand, modelYear);

        assertEquals(licensePlate, vehicle.getLicensePlate());
        assertEquals(model, vehicle.getModel());
        assertEquals(brand, vehicle.getBrand());
        assertEquals(modelYear, vehicle.getModelYear());
    }

    @Test
    @DisplayName("Deve atualizar o modelo do veículo com sucesso")
    void shouldUpdateVehicleModel() {
        Vehicle vehicle = Vehicle.create("Fox", "ABC1234", "VW", Short.valueOf("2015"));
        String newModel = "Corolla";
        String newBrand = "Toyota";
        Short newYear = Short.valueOf("2015");

        Vehicle updatedVehicle = vehicle.update(newModel, newBrand, newYear);

        assertEquals(newModel, updatedVehicle.getModel());
        assertEquals("Fox", vehicle.getModel());
    }

    @Test
    @DisplayName("Deve restaurar um veículo a partir de dados existentes")
    void shouldRestoreVehicle() {
        var now = LocalDateTime.now();

        var vehicle = Vehicle.restore("Civic", "ABC1234", "Honda", (short) 2023, null, now, now);

        assertEquals("ABC1234", vehicle.getLicensePlate());
        assertEquals("Civic", vehicle.getModel());
        assertEquals("Honda", vehicle.getBrand());
        assertEquals(Short.valueOf((short) 2023), vehicle.getModelYear());
        assertEquals(now, vehicle.getDateCreated());
        assertEquals(now, vehicle.getDateUpdated());
    }

    @Test
    @DisplayName("Deve lançar exceção para modelo nulo")
    void shouldThrowExceptionForNullModel() {
        assertThrows(VehicleExceptions.ModelNotEmpty.class, () ->
                Vehicle.create(null, "ABC1234", "Honda", (short) 2023));
    }

    @Test
    @DisplayName("Deve lançar exceção para marca nula")
    void shouldThrowExceptionForNullBrand() {
        assertThrows(VehicleExceptions.BrandNotEmpty.class, () ->
                Vehicle.create("Civic", "ABC1234", null, (short) 2023));
    }

    @Test
    @DisplayName("Deve lançar exceção para ano do modelo nulo")
    void shouldThrowExceptionForNullModelYear() {
        assertThrows(VehicleExceptions.InvalidModelYear.class, () ->
                Vehicle.create("Civic", "ABC1234", "Honda", null));
    }

    @Test
    @DisplayName("Deve lançar exceção para ano do modelo inválido")
    void shouldThrowExceptionForInvalidModelYear() {
        assertThrows(VehicleExceptions.InvalidModelYear.class, () ->
                Vehicle.create("Civic", "ABC1234", "Honda", (short) 1800));
    }
}
