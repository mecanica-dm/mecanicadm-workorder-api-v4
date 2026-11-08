package com.mecanicadm.workorder_api.core.vehicle.exception;

import com.mecanicadm.workorder_api.shared.exception.DomainExceptionCore;

public final class VehicleExceptions {

    private VehicleExceptions() {
    }

    public static class LicensePlateNotEmpty extends DomainExceptionCore {
        public LicensePlateNotEmpty() { super("validation.vehicle.licensePlate.not.blank", 400); }
    }

    public static class InvalidLicensePlate extends DomainExceptionCore {
        public InvalidLicensePlate() { super("validation.vehicle.licensePlate.invalid", 400); }
    }

    public static class ModelNotEmpty extends DomainExceptionCore {
        public ModelNotEmpty() { super("validation.vehicle.model.not.blank", 400); }
    }

    public static class BrandNotEmpty extends DomainExceptionCore {
        public BrandNotEmpty() { super("validation.vehicle.brand.not.blank", 400); }
    }

    public static class InvalidModelYear extends DomainExceptionCore {
        public InvalidModelYear() { super("validation.vehicle.modelYear.invalid", 400); }
    }

    public static class NotFound extends DomainExceptionCore {
        public NotFound() { super("vehicle.not.found", 404); }
    }

    public static class VehicleExists extends DomainExceptionCore {
        public VehicleExists(String licensePlate) { super("vehicle.exists", 400, licensePlate); }
        public VehicleExists() { super("vehicle.licensePlate.exists", 400); }
    }
}

