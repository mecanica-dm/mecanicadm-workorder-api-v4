package com.mecanicadm.workorder_api.core.workorder.domain.events;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mecanicadm.workorder_api.core.material.domain.enums.MaterialType;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.enums.LaborExecutionStatus;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderDetails;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderLaborItem;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderMaterialItem;
import com.mecanicadm.workorder_api.infra.messaging.serialization.MessageJsonSerializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorkOrderEventContractTest {
    private final MessageJsonSerializer serializer = new MessageJsonSerializer();
    private final ObjectMapper reader = new ObjectMapper();

    private final UUID laborId = UUID.randomUUID();
    private final UUID materialId = UUID.randomUUID();

    private static final String[] ORDER_FIELDS = {"client", "vehicle", "description", "status", "executionStartAt",
            "executionEndAt", "laborItems", "materialItems"};

    private WorkOrderDetails details(WorkOrder workOrder) {
        return new WorkOrderDetails(workOrder.getId(),
                new ClientSnapshot(workOrder.getClientId(), "Cliente Teste", "cliente@test.com", "12345678900", "48999999999"),
                new VehicleSnapshot(workOrder.getVehicleId(), "Volvo FH", "Volvo", 2020),
                workOrder.getDescription(), workOrder.getStatus(), null, null, laborItems(), materialItems());
    }

    private List<LaborItemSnapshot> laborItems() {
        return List.of(new LaborItemSnapshot(UUID.randomUUID(), laborId, "Troca de oleo", new BigDecimal("150.00"),
                LaborExecutionStatus.AWAITING_EXECUTION, null, null));
    }

    private List<MaterialItemSnapshot> materialItems() {
        return List.of(new MaterialItemSnapshot(UUID.randomUUID(), materialId, "Filtro de oleo", "Bosch", "Filtro",
                MaterialType.values()[0], new BigDecimal("40.00"), 2, new BigDecimal("80.00")));
    }

    private WorkOrder workOrder() {
        WorkOrder workOrder = WorkOrder.create(UUID.randomUUID(), "ABC-1234", "Troca de oleo");
        Set<WorkOrderLaborItem> labor = new HashSet<>();
        labor.add(WorkOrderLaborItem.create(laborId, workOrder.getId()));
        Set<WorkOrderMaterialItem> material = new HashSet<>();
        material.add(WorkOrderMaterialItem.create(materialId, 2));
        return WorkOrder.restore(workOrder.getId(), workOrder.getClientId(), workOrder.getVehicleId(), workOrder.getDescription(),
                workOrder.getStatus(), null, null, labor, material, workOrder.getDateCreated(), workOrder.getDateUpdated(), null,
                null);
    }

    private Set<String> fields(Object event) throws Exception {
        JsonNode json = reader.readTree(serializer.toJson(event));
        Set<String> names = new TreeSet<>();
        json.fieldNames().forEachRemaining(names::add);
        return names;
    }

    private void assertContract(Function<WorkOrder, Object> factory, String... ownFields) throws Exception {
        Set<String> expected = new TreeSet<>(Set.of("workOrderId"));
        expected.addAll(Set.of(ownFields));
        assertEquals(expected, fields(factory.apply(workOrder())));
    }

    @Test
    @DisplayName("Deve respeitar o contrato dos eventos que levam apenas o cabecalho")
    void shouldKeepHeaderOnlyContractForSimpleEvents() throws Exception {
        assertContract(WorkOrderExecutionStartedEvent::of);
        assertContract(WorkOrderVehicleDeliveredEvent::of);
        assertContract(WorkOrderVehicleReleasedEvent::of);
        assertContract(WorkOrderAuthorizedEvent::of);
        assertContract(WorkOrderDiagnosedEvent::of);
        assertContract(WorkOrderExecutionFinishedEvent::of);
        assertContract(WorkOrderCanceledEvent::of);
    }

    @Test
    @DisplayName("Deve respeitar o contrato dos eventos com campo proprio")
    void shouldKeepOwnFieldContractForEventsWithExtraData() throws Exception {
        assertContract(w -> WorkOrderChangesRequestedEvent.of(w, "obs"), "observation");
    }

    @Test
    @DisplayName("Deve respeitar o contrato dos eventos Created e Updated, que carregam os dados da OS")
    void shouldKeepSnapshotContractForFullObjectEvents() throws Exception {
        assertContract(w -> WorkOrderCreatedEvent.of(details(w)), ORDER_FIELDS);
        assertContract(w -> WorkOrderUpdatedEvent.of(details(w)), ORDER_FIELDS);

    }

    @Test
    @DisplayName("Deve incluir nome, preco e quantidade de mao de obra e materiais no retrato da OS")
    void shouldIncludeBudgetDataOfLaborAndMaterialItemsInSnapshot() throws Exception {
        JsonNode snapshot = reader.readTree(serializer.toJson(WorkOrderCreatedEvent.of(details(workOrder()))));

        Set<String> names = new TreeSet<>();
        snapshot.fieldNames().forEachRemaining(names::add);
        assertEquals(new TreeSet<>(Set.of("workOrderId", "client", "vehicle", "description", "status", "executionStartAt", "executionEndAt", "laborItems",
                "materialItems")), names);

        JsonNode client = snapshot.get("client");
        assertEquals("Cliente Teste", client.get("name").asText());
        assertEquals("cliente@test.com", client.get("email").asText());
        assertEquals("12345678900", client.get("document").asText());
        assertEquals("48999999999", client.get("phone").asText());
        assertTrue(client.has("id"));

        JsonNode vehicle = snapshot.get("vehicle");
        assertEquals("ABC-1234", vehicle.get("licensePlate").asText());
        assertEquals("Volvo FH", vehicle.get("model").asText());
        assertEquals("Volvo", vehicle.get("brand").asText());
        assertEquals(2020, vehicle.get("modelYear").asInt());

        JsonNode labor = snapshot.get("laborItems").get(0);
        assertEquals(1, snapshot.get("laborItems").size());
        assertEquals("Troca de oleo", labor.get("name").asText());
        assertEquals(0, new BigDecimal("150.00").compareTo(labor.get("price").decimalValue()));
        assertTrue(labor.has("laborId"));

        JsonNode material = snapshot.get("materialItems").get(0);
        assertEquals(1, snapshot.get("materialItems").size());
        assertEquals("Filtro de oleo", material.get("name").asText());
        assertEquals("Bosch", material.get("brand").asText());
        assertEquals(2, material.get("quantity").asInt());
        assertEquals(0, new BigDecimal("40.00").compareTo(material.get("unitPrice").decimalValue()));
        assertEquals(0, new BigDecimal("80.00").compareTo(material.get("totalPrice").decimalValue()));

    }

    @Test
    @DisplayName("Deve preencher o corpo so com o id da OS, sem messageId, eventType nem occurredAt")
    void shouldFillBodyWithWorkOrderIdAndIsoUtcOccurredAt() throws Exception {
        WorkOrder workOrder = workOrder();
        JsonNode json = reader.readTree(serializer.toJson(WorkOrderAuthorizedEvent.of(workOrder)));

        assertEquals(workOrder.getId().toString(), json.get("workOrderId").asText());
        assertFalse(json.has("messageId"));
        assertFalse(json.has("eventType"));
        assertFalse(json.has("occurredAt"));
    }
}
