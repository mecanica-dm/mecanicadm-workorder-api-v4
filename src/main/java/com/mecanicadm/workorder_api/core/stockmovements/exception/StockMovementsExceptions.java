package com.mecanicadm.workorder_api.core.stockmovements.exception;

import com.mecanicadm.workorder_api.shared.exception.DomainExceptionCore;

public class StockMovementsExceptions {

    private StockMovementsExceptions() {
    }

    public static class NotFound extends DomainExceptionCore {
        public NotFound() {
            super("stock.not.found", 404);
        }
    }

    public static class InvalidQuantity extends DomainExceptionCore {
        public InvalidQuantity() {
            super("stock.quantity.invalid", 400);
        }
    }

    public static class InsufficientStock extends DomainExceptionCore {
        public InsufficientStock() {
            super("stock.quantity.insufficient", 400);
        }
    }
}
