package com.mecanicadm.workorder_api.core.material.exception;

import com.mecanicadm.workorder_api.shared.exception.DomainExceptionCore;

public final class MaterialExceptions {

    private MaterialExceptions() {
    }

    public static class MaterialNotFound extends DomainExceptionCore {
        public MaterialNotFound() {
            super("material.not.found", 404);
        }
    }

    public static class NameRequired extends DomainExceptionCore {
        public NameRequired() {
            super("validation.material.name.not.blank", 400);
        }
    }

    public static class PriceRequired extends DomainExceptionCore {
        public PriceRequired() {
            super("validation.material.price.required", 400);
        }
    }

    public static class TypeRequired extends DomainExceptionCore {
        public TypeRequired() {
            super("validation.material.type.required", 400);
        }
    }
}
