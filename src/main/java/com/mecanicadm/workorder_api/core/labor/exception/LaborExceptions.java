package com.mecanicadm.workorder_api.core.labor.exception;

import com.mecanicadm.workorder_api.shared.exception.DomainExceptionCore;

public final class LaborExceptions {

    private LaborExceptions() {
    }

    public static class LaborNotFound extends DomainExceptionCore {
        public LaborNotFound() {
            super("labor.not.found", 404);
        }
    }

    public static class NameRequired extends DomainExceptionCore {
        public NameRequired() {
            super("validation.labor.name.not.blank", 400);
        }
    }

    public static class PriceRequired extends DomainExceptionCore {
        public PriceRequired() {
            super("validation.labor.price.required", 400);
        }
    }
}
