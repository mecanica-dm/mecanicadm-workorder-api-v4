package com.mecanicadm.workorder_api.core.workorder.exception;

import com.mecanicadm.workorder_api.shared.exception.DomainExceptionCore;

public class WorkOrderExceptions {

    private WorkOrderExceptions() {
    }

    public static class NotFound extends DomainExceptionCore {
        public NotFound() {
            super("work.order.not.found", 404);
        }
    }

    public static class LaborItemNotFound extends DomainExceptionCore {
        public LaborItemNotFound() {
            super("work.order.labor.item.not.found", 404);
        }
    }

    public static class InvalidMaterialQuantity extends DomainExceptionCore {
        public InvalidMaterialQuantity() {
            super("work.order.material.quantity.invalid", 400);
        }
    }

    public static class InvalidStatusTransition extends DomainExceptionCore {
        public InvalidStatusTransition(String from, String to) {
            super("work.order.status.transition.invalid", 400, from, to);
        }
    }

    public static class InvalidLaborStatusTransition extends DomainExceptionCore {
        public InvalidLaborStatusTransition(String from, String to) {
            super("work.order.labor.status.transition.invalid", 400, from, to);
        }
    }

    public static class InvalidStatus extends DomainExceptionCore {
        public InvalidStatus(String status) {
            super("work.order.status.invalid", 400, status);
        }
    }

    public static class LaborCannotStartIfNotInExecution extends DomainExceptionCore {
        public LaborCannotStartIfNotInExecution() {
            super("work.order.labor.start.not.in.execution", 400);
        }
    }

    public static class PendingLaborItems extends DomainExceptionCore {
        public PendingLaborItems() {
            super("work.order.labor.pending.items", 400);
        }
    }

    public static class LaborItemsRequired extends DomainExceptionCore {
        public LaborItemsRequired() {
            super("work.order.labor.required", 400);
        }
    }

    public static class ClientRequired extends DomainExceptionCore {
        public ClientRequired() {
            super("work.order.client.required", 400);
        }
    }

    public static class VehicleRequired extends DomainExceptionCore {
        public VehicleRequired() {
            super("work.order.vehicle.required", 400);
        }
    }

    public static class InvalidReportPeriod extends DomainExceptionCore {
        public InvalidReportPeriod() {
            super("work.order.report.period.invalid", 400);
        }
    }

    public static class LaborIdRequired extends DomainExceptionCore {
        public LaborIdRequired() {
            super("work.order.labor.id.required", 400);
        }
    }

    public static class MaterialIdRequired extends DomainExceptionCore {
        public MaterialIdRequired() {
            super("work.order.material.id.required", 400);
        }
    }

    public static class WorkOrderIdRequired extends DomainExceptionCore {
        public WorkOrderIdRequired() {
            super("work.order.id.required", 400);
        }
    }
}
