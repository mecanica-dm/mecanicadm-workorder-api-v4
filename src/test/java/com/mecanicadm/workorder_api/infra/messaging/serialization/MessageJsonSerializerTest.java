package com.mecanicadm.workorder_api.infra.messaging.serialization;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageJsonSerializerTest {

    private final MessageJsonSerializer serializer = new MessageJsonSerializer();

    @Test
    @DisplayName("Deve escrever a data em ISO-8601 e nunca como timestamp numerico")
    void shouldWriteDatesAsIsoText() throws Exception {
        String json = serializer.toJson(java.util.Map.of("at", java.time.ZonedDateTime.parse("2026-10-31T14:03:11Z")));

        assertTrue(json.matches(".*\"at\":\"\\d{4}-\\d{2}-\\d{2}T[^\"]+\".*"));
    }
}
