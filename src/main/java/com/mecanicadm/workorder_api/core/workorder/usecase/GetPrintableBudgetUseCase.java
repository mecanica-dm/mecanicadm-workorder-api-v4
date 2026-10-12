package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.shared.usecase.UseCase;

import com.mecanicadm.workorder_api.core.client.domain.Client;
import com.mecanicadm.workorder_api.core.client.domain.port.ClientGateway;
import com.mecanicadm.workorder_api.core.client.exception.ClientExceptions;
import com.mecanicadm.workorder_api.core.labor.domain.Labor;
import com.mecanicadm.workorder_api.core.labor.domain.port.LaborGateway;
import com.mecanicadm.workorder_api.core.material.domain.Material;
import com.mecanicadm.workorder_api.core.material.domain.port.MaterialGateway;
import com.mecanicadm.workorder_api.core.vehicle.domain.Vehicle;
import com.mecanicadm.workorder_api.core.vehicle.domain.port.VehicleGateway;
import com.mecanicadm.workorder_api.core.vehicle.exception.VehicleExceptions;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrder;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderBudget;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderLaborItem;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderMaterialItem;
import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.exception.WorkOrderExceptions;
import com.mecanicadm.workorder_api.core.workorder.usecase.dto.PrintableBudgetDTO;
import com.mecanicadm.workorder_api.core.workorder.usecase.dto.PrintableBudgetResponse;
import com.mecanicadm.workorder_api.core.workorder.usecase.dto.PrintableClientDTO;
import com.mecanicadm.workorder_api.core.workorder.usecase.dto.PrintableLaborItemDTO;
import com.mecanicadm.workorder_api.core.workorder.usecase.dto.PrintableMaterialItemDTO;
import com.mecanicadm.workorder_api.core.workorder.usecase.dto.PrintableVehicleDTO;
import com.mecanicadm.workorder_api.core.workorder.usecase.query.GetPrintableBudgetQuery;
import com.mecanicadm.workorder_api.infra.pdf.PdfGenerator;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GetPrintableBudgetUseCase implements UseCase<GetPrintableBudgetQuery, PrintableBudgetResponse> {
    private final WorkOrderGateway workOrderGateway;
    private final ClientGateway clientGateway;
    private final VehicleGateway vehicleGateway;
    private final LaborGateway laborGateway;
    private final MaterialGateway materialGateway;
    private final PdfGenerator pdfGenerator;

    public GetPrintableBudgetUseCase(WorkOrderGateway workOrderGateway,
                                     ClientGateway clientGateway,
                                     VehicleGateway vehicleGateway,
                                     LaborGateway laborGateway,
                                     MaterialGateway materialGateway,
                                     PdfGenerator pdfGenerator) {
        this.workOrderGateway = workOrderGateway;
        this.clientGateway = clientGateway;
        this.vehicleGateway = vehicleGateway;
        this.laborGateway = laborGateway;
        this.materialGateway = materialGateway;
        this.pdfGenerator = pdfGenerator;
    }

    public PrintableBudgetResponse execute(GetPrintableBudgetQuery query) {
        WorkOrder workOrder = getWorkOrder(query.workOrderId());
        PrintableClientDTO clientDTO = buildClientDTO(workOrder.getClientId());
        PrintableVehicleDTO vehicleDTO = buildVehicleDTO(workOrder.getVehicleId());

        List<PrintableLaborItemDTO> laborItems = buildLaborItems(workOrder.getLaborItems());
        List<PrintableMaterialItemDTO> materialItems = buildMaterialItems(workOrder.getMaterialItems());

        BigDecimal totalLabor = calculateTotalLabor(laborItems);
        BigDecimal totalMaterial = calculateTotalMaterial(materialItems);
        BigDecimal totalBudget = calculateTotalBudget(workOrder, totalLabor, totalMaterial);

        PrintableBudgetDTO budgetDTO = new PrintableBudgetDTO(
                clientDTO,
                vehicleDTO,
                laborItems,
                materialItems,
                totalLabor,
                totalMaterial,
                totalBudget
        );

        return generatePdfResponse(workOrder.getId(), budgetDTO);
    }

    private WorkOrder getWorkOrder(UUID workOrderId) {
        return workOrderGateway.findByIdWithItems(workOrderId)
                .orElseThrow(WorkOrderExceptions.NotFound::new);
    }

    private PrintableClientDTO buildClientDTO(UUID clientId) {
        Client client = clientGateway.findById(clientId)
                .orElseThrow(ClientExceptions.NotFound::new);

        return new PrintableClientDTO(
                client.getName(),
                formatDocument(client.getDocument()),
                formatPhone(client.getPhone()),
                client.getEmail()
        );
    }

    private PrintableVehicleDTO buildVehicleDTO(String vehicleId) {
        Vehicle vehicle = vehicleGateway.findByLicensePlate(vehicleId)
                .orElseThrow(VehicleExceptions.NotFound::new);

        return new PrintableVehicleDTO(
                vehicle.getLicensePlate(),
                vehicle.getModel()
        );
    }

    private List<PrintableLaborItemDTO> buildLaborItems(Set<WorkOrderLaborItem> laborItems) {
        Set<UUID> laborIds = laborItems.stream()
                .map(WorkOrderLaborItem::getLaborId)
                .collect(Collectors.toSet());

        Map<UUID, Labor> laborsById = laborGateway.findAllByIds(laborIds).stream()
                .collect(Collectors.toMap(Labor::getId, Function.identity()));

        return laborItems.stream()
                .map(item -> {
                    Labor labor = laborsById.get(item.getLaborId());
                    return new PrintableLaborItemDTO(labor.getName(), labor.getPrice());
                })
                .toList();
    }

    private List<PrintableMaterialItemDTO> buildMaterialItems(Set<WorkOrderMaterialItem> materialItems) {
        Set<UUID> materialIds = materialItems.stream()
                .map(WorkOrderMaterialItem::getMaterialId)
                .collect(Collectors.toSet());

        Map<UUID, Material> materialsById = materialGateway.findAllByIds(materialIds).stream()
                .collect(Collectors.toMap(Material::getId, Function.identity()));

        return materialItems.stream()
                .map(item -> {
                    Material material = materialsById.get(item.getMaterialId());
                    BigDecimal totalPrice = material.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                    return new PrintableMaterialItemDTO(material.getName(), item.getQuantity(), material.getPrice(), totalPrice);
                })
                .toList();
    }

    private BigDecimal calculateTotalLabor(List<PrintableLaborItemDTO> laborItems) {
        return laborItems.stream()
                .map(PrintableLaborItemDTO::price)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTotalMaterial(List<PrintableMaterialItemDTO> materialItems) {
        return materialItems.stream()
                .map(PrintableMaterialItemDTO::totalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTotalBudget(WorkOrder workOrder, BigDecimal totalLabor, BigDecimal totalMaterial) {
        return workOrder.getBudget()
                .map(WorkOrderBudget::getTotalPrice)
                .orElseGet(() -> totalLabor.add(totalMaterial));
    }

    private PrintableBudgetResponse generatePdfResponse(UUID workOrderId, PrintableBudgetDTO budgetDTO) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("budget", budgetDTO);

        byte[] pdfBytes = pdfGenerator.generatePdfFromHtml("budget-template", variables);
        String base64Content = Base64.getEncoder().encodeToString(pdfBytes);
        String fileName = "orcamento-os-" + workOrderId.toString().substring(0, 8) + ".pdf";

        return new PrintableBudgetResponse(fileName, base64Content);
    }

    private String formatDocument(String document) {
        if (document == null) return "";
        String numbers = document.replaceAll("\\D", "");
        if (numbers.length() == 11) {
            return numbers.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        } else if (numbers.length() == 14) {
            return numbers.replaceFirst("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
        }
        return document;
    }

    private String formatPhone(String phone) {
        if (phone == null) return "";
        String numbers = phone.replaceAll("\\D", "");
        if (numbers.length() == 11) {
            return numbers.replaceFirst("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
        } else if (numbers.length() == 10) {
            return numbers.replaceFirst("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
        }
        return phone;
    }
}
