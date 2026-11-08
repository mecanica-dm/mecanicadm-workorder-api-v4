package com.mecanicadm.workorder_api.core.vehicle.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class VehicleExceptionsTest {

    @Test
    @DisplayName("Deve cobrir o construtor privado de VehicleExceptions")
    void shouldCoverPrivateConstructor() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<VehicleExceptions> constructor = VehicleExceptions.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        VehicleExceptions instance = constructor.newInstance();
        assertNotNull(instance);
    }

    @Test
    @DisplayName("Deve instanciar todas as exceções de Vehicle para garantir cobertura")
    void shouldInstantiateAllExceptions() {
        var licensePlateNotEmpty = new VehicleExceptions.LicensePlateNotEmpty();
        assertEquals(400, licensePlateNotEmpty.getStatus());
        assertEquals("validation.vehicle.licensePlate.not.blank", licensePlateNotEmpty.getMessageKey());

        var invalidLicensePlate = new VehicleExceptions.InvalidLicensePlate();
        assertEquals(400, invalidLicensePlate.getStatus());
        assertEquals("validation.vehicle.licensePlate.invalid", invalidLicensePlate.getMessageKey());

        var modelNotEmpty = new VehicleExceptions.ModelNotEmpty();
        assertEquals(400, modelNotEmpty.getStatus());
        assertEquals("validation.vehicle.model.not.blank", modelNotEmpty.getMessageKey());

        var brandNotEmpty = new VehicleExceptions.BrandNotEmpty();
        assertEquals(400, brandNotEmpty.getStatus());
        assertEquals("validation.vehicle.brand.not.blank", brandNotEmpty.getMessageKey());

        var invalidModelYear = new VehicleExceptions.InvalidModelYear();
        assertEquals(400, invalidModelYear.getStatus());
        assertEquals("validation.vehicle.modelYear.invalid", invalidModelYear.getMessageKey());

        var notFound = new VehicleExceptions.NotFound();
        assertEquals(404, notFound.getStatus());
        assertEquals("vehicle.not.found", notFound.getMessageKey());

        var vehicleExists = new VehicleExceptions.VehicleExists();
        assertEquals(400, vehicleExists.getStatus());
        assertEquals("vehicle.licensePlate.exists", vehicleExists.getMessageKey());

        var vehicleExistsWithPlate = new VehicleExceptions.VehicleExists("ABC1234");
        assertEquals(400, vehicleExistsWithPlate.getStatus());
        assertEquals("vehicle.exists", vehicleExistsWithPlate.getMessageKey());
    }
}
