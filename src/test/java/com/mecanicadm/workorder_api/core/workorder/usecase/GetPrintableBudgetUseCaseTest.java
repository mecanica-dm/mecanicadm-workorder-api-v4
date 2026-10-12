package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.client.domain.Client;
import com.mecanicadm.workorder_api.core.client.domain.port.ClientGateway;
import com.mecanicadm.workorder_api.core.client.exception.ClientExceptions;
import com.mecanicadm.workorder_api.core.labor.domain.Labor;
import com.mecanicadm.workorder_api.core.labor.domain.port.LaborGateway;
import com.mecanicadm.workorder_api.core.material.domain.Material;
import com.mecanicadm.workorder_api.core.material.domain.port.MaterialGateway;
import com.mecanicadm.workorder_api.core.vehicle.domain.Vehicle;
import com.mecanicadm.workorder_api.core.vehicle.domain.port.VehicleGateway;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderBudget;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderLaborItem;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderMaterialItem;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.dto.PrintableBudgetResponse;
import com.mecanicadm.workorder_api.core.workorder.usecase.query.GetPrintableBudgetQuery;
import com.mecanicadm.workorder_api.infra.pdf.PdfGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetPrintableBudgetUseCaseTest {

    @Mock
    private WorkOrderGateway workOrderGateway;

    @Mock
    private ClientGateway clientGateway;

    @Mock
    private VehicleGateway vehicleGateway;

    @Mock
    private LaborGateway laborGateway;

    @Mock
    private MaterialGateway materialGateway;

    @Mock
    private PdfGenerator pdfGenerator;

    @InjectMocks
    private GetPrintableBudgetUseCase useCase;

    @Test
    @DisplayName("Deve gerar PDF do orçamento com todos os dados")
    void shouldGeneratePrintableBudgetSuccessfully() {
        UUID workOrderId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        String vehicleId = "ABC-1234";
        UUID laborId = UUID.randomUUID();
        UUID materialId = UUID.randomUUID();

        WorkOrder workOrder = mock(WorkOrder.class);
        when(workOrder.getClientId()).thenReturn(clientId);
        when(workOrder.getVehicleId()).thenReturn(vehicleId);
        when(workOrder.getId()).thenReturn(workOrderId);

        WorkOrderLaborItem laborItem = mock(WorkOrderLaborItem.class);
        when(laborItem.getLaborId()).thenReturn(laborId);
        when(workOrder.getLaborItems()).thenReturn(Set.of(laborItem));

        WorkOrderMaterialItem materialItem = mock(WorkOrderMaterialItem.class);
        when(materialItem.getMaterialId()).thenReturn(materialId);
        when(materialItem.getQuantity()).thenReturn(2);
        when(workOrder.getMaterialItems()).thenReturn(Set.of(materialItem));

        WorkOrderBudget budget = mock(WorkOrderBudget.class);
        when(budget.getTotalPrice()).thenReturn(new BigDecimal("500.00"));
        when(workOrder.getBudget()).thenReturn(Optional.of(budget));

        when(workOrderGateway.findByIdWithItems(workOrderId)).thenReturn(Optional.of(workOrder));

        Client client = mock(Client.class);
        when(client.getName()).thenReturn("João Silva");
        when(client.getDocument()).thenReturn("12345678901");
        when(client.getPhone()).thenReturn("11987654321");
        when(client.getEmail()).thenReturn("joao@email.com");
        when(clientGateway.findById(clientId)).thenReturn(Optional.of(client));

        Vehicle vehicle = mock(Vehicle.class);
        when(vehicle.getLicensePlate()).thenReturn(vehicleId);
        when(vehicle.getModel()).thenReturn("Fusca 1978");
        when(vehicleGateway.findByLicensePlate(vehicleId)).thenReturn(Optional.of(vehicle));

        Labor labor = mock(Labor.class);
        when(labor.getId()).thenReturn(laborId);
        when(labor.getName()).thenReturn("Troca de óleo");
        when(labor.getPrice()).thenReturn(new BigDecimal("100.00"));
        when(laborGateway.findAllByIds(Set.of(laborId))).thenReturn(List.of(labor));

        Material material = mock(Material.class);
        when(material.getId()).thenReturn(materialId);
        when(material.getName()).thenReturn("Óleo 5W30");
        when(material.getPrice()).thenReturn(new BigDecimal("50.00"));
        when(materialGateway.findAllByIds(Set.of(materialId))).thenReturn(List.of(material));

        byte[] pdfBytes = "PDF_CONTENT".getBytes();
        when(pdfGenerator.generatePdfFromHtml(anyString(), anyMap())).thenReturn(pdfBytes);

        PrintableBudgetResponse response = useCase.execute(new GetPrintableBudgetQuery(workOrderId));

        assertNotNull(response);
        assertTrue(response.fileName().startsWith("orcamento-os-"));
        assertTrue(response.fileName().endsWith(".pdf"));
        assertFalse(response.base64Content().isEmpty());

        verify(workOrderGateway).findByIdWithItems(workOrderId);
        verify(clientGateway).findById(clientId);
        verify(vehicleGateway).findByLicensePlate(vehicleId);
        verify(laborGateway).findAllByIds(Set.of(laborId));
        verify(materialGateway).findAllByIds(Set.of(materialId));
        verify(pdfGenerator).generatePdfFromHtml(eq("budget-template"), anyMap());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a ordem de serviço não for encontrada")
    void shouldThrowExceptionWhenWorkOrderNotFound() {
        UUID workOrderId = UUID.randomUUID();
        when(workOrderGateway.findByIdWithItems(workOrderId)).thenReturn(Optional.empty());

        assertThrows(WorkOrderExceptions.NotFound.class,
                () -> useCase.execute(new GetPrintableBudgetQuery(workOrderId)));

        verifyNoInteractions(clientGateway, vehicleGateway, laborGateway, materialGateway, pdfGenerator);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o cliente não for encontrado")
    void shouldThrowExceptionWhenClientNotFound() {
        UUID workOrderId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        WorkOrder workOrder = mock(WorkOrder.class);
        when(workOrder.getClientId()).thenReturn(clientId);

        when(workOrderGateway.findByIdWithItems(workOrderId)).thenReturn(Optional.of(workOrder));
        when(clientGateway.findById(clientId)).thenReturn(Optional.empty());

        assertThrows(ClientExceptions.NotFound.class,
                () -> useCase.execute(new GetPrintableBudgetQuery(workOrderId)));
    }
}
