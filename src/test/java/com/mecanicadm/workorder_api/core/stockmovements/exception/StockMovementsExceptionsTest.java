package com.mecanicadm.workorder_api.core.stockmovements.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StockMovementsExceptionsTest {

    @Test
    @DisplayName("Deve cobrir o construtor privado de StockMovementsExceptions")
    void shouldCoverPrivateConstructor() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<StockMovementsExceptions> constructor = StockMovementsExceptions.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        StockMovementsExceptions instance = constructor.newInstance();
        assertNotNull(instance);
    }

    @Test
    @DisplayName("Deve instanciar todas as exceções de StockMovements para garantir cobertura")
    void shouldInstantiateAllExceptions() {
        var invalidQuantity = new StockMovementsExceptions.InvalidQuantity();
        assertEquals(400, invalidQuantity.getStatus());
        assertEquals("stock.quantity.invalid", invalidQuantity.getMessageKey());

        var insufficientStock = new StockMovementsExceptions.InsufficientStock();
        assertEquals(400, insufficientStock.getStatus());
        assertEquals("stock.quantity.insufficient", insufficientStock.getMessageKey());

        var notFound = new StockMovementsExceptions.NotFound();
        assertEquals(404, notFound.getStatus());
        assertEquals("stock.not.found", notFound.getMessageKey());
    }
}
