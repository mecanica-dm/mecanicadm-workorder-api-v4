package com.mecanicadm.workorder_api.infra.features.vehicle.api;

import com.mecanicadm.workorder_api.core.vehicle.usecase.*;
import com.mecanicadm.workorder_api.core.vehicle.usecase.command.CreateVehicleCommand;
import com.mecanicadm.workorder_api.core.vehicle.usecase.command.DeleteVehicleCommand;
import com.mecanicadm.workorder_api.core.vehicle.usecase.command.UpdateVehicleCommand;
import com.mecanicadm.workorder_api.core.vehicle.usecase.query.GetVehicleByIdQuery;
import com.mecanicadm.workorder_api.infra.features.vehicle.api.dto.request.CreateVehicleRequest;
import com.mecanicadm.workorder_api.infra.features.vehicle.api.dto.request.UpdateVehicleRequest;
import com.mecanicadm.workorder_api.infra.features.vehicle.api.dto.response.VehicleResponse;
import com.mecanicadm.workorder_api.infra.features.vehicle.api.openapi.VehicleOpenApi;
import com.mecanicadm.workorder_api.shared.validation.annotation.LicensePlate;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/vehicle")
@Validated
public class VehicleController extends VehicleOpenApi {

    private final CreateVehicleUseCase createVehicleUseCase;
    private final UpdateVehicleUseCase updateVehicleUseCase;
    private final DeleteVehicleUseCase deleteVehicleUseCase;
    private final GetVehicleByIdUseCase getVehicleByIdUseCase;
    private final GetAllVehicleUseCase getAllVehicleUseCase;

    public VehicleController(CreateVehicleUseCase createVehicleUseCase,
                             UpdateVehicleUseCase updateVehicleUseCase,
                             DeleteVehicleUseCase deleteVehicleUseCase,
                             GetVehicleByIdUseCase getVehicleByIdUseCase,
                             GetAllVehicleUseCase getAllVehicleUseCase) {
        this.createVehicleUseCase = createVehicleUseCase;
        this.updateVehicleUseCase = updateVehicleUseCase;
        this.deleteVehicleUseCase = deleteVehicleUseCase;
        this.getVehicleByIdUseCase = getVehicleByIdUseCase;
        this.getAllVehicleUseCase = getAllVehicleUseCase;
    }

    @PostMapping
    public ResponseEntity<String> create(@Valid @RequestBody CreateVehicleRequest request) {
        var command = new CreateVehicleCommand(request.model(), request.licensePlate(), request.brand(), request.modelYear());
        String licensePlate = createVehicleUseCase.execute(command);
        URI location = URI.create(String.format("/vehicle/%s", licensePlate));
        return ResponseEntity.created(location).body(licensePlate);
    }

    @PutMapping("/{licensePlate}")
    public ResponseEntity<Void> update(@PathVariable @LicensePlate String licensePlate, @Valid @RequestBody UpdateVehicleRequest request) {
        var command = new UpdateVehicleCommand(licensePlate, request.model(), request.brand(), request.modelYear());
        updateVehicleUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{licensePlate}")
    public ResponseEntity<Void> delete(@PathVariable @LicensePlate String licensePlate) {
        deleteVehicleUseCase.execute(new DeleteVehicleCommand(licensePlate));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{licensePlate}")
    public ResponseEntity<VehicleResponse> findById(@PathVariable @LicensePlate String licensePlate) {
        var vehicle = getVehicleByIdUseCase.execute(new GetVehicleByIdQuery(licensePlate));
        return ResponseEntity.ok(VehicleResponse.from(vehicle));
    }

    @GetMapping
    public ResponseEntity<Page<VehicleResponse>> getAll(
            @RequestParam(required = false) String licensePlate,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Short modelYear,
            @PageableDefault(size = 20) Pageable pageable) {

        var query = VehicleQueryMapper.toQuery(licensePlate, model, brand, modelYear, pageable);
        var result = getAllVehicleUseCase.execute(query);
        return ResponseEntity.ok(VehicleQueryMapper.toPage(result, pageable));
    }
}
