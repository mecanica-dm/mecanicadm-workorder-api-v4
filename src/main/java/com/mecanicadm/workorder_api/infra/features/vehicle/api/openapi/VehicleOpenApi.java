package com.mecanicadm.workorder_api.infra.features.vehicle.api.openapi;

import com.mecanicadm.workorder_api.infra.features.vehicle.api.dto.request.CreateVehicleRequest;
import com.mecanicadm.workorder_api.infra.features.vehicle.api.dto.request.UpdateVehicleRequest;
import com.mecanicadm.workorder_api.infra.features.vehicle.api.dto.response.VehicleResponse;
import com.mecanicadm.workorder_api.shared.validation.annotation.LicensePlate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Veículos", description = "Endpoints para gerenciamento de veículos")
public abstract class VehicleOpenApi {
    @PostMapping
    @Operation(summary = "Criar novo veículo", description = "Cadastra um novo veículo no sistema")
    @ApiResponse(responseCode = "201", description = "Veículo cadastrado com sucesso")
    public abstract ResponseEntity<String> create(@Valid @RequestBody CreateVehicleRequest cmd);

    @PutMapping("/{licensePlate}")
    @Operation(summary = "Atualizar veículo", description = "Atualiza os dados de um veículo existente através da placa")
    @ApiResponse(responseCode = "204", description = "Veículo atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Veículo não encontrado")
    public abstract ResponseEntity<Void> update(@Parameter(description = "Identificador do veículo a ser atualizado") @PathVariable @LicensePlate String licensePlate, @Valid @RequestBody UpdateVehicleRequest cmd);

    @DeleteMapping("/{licensePlate}")
    @Operation(summary = "Excluir veículo", description = "Exclui um veículo existente através da placa")
    @ApiResponse(responseCode = "204", description = "Veículo excluído com sucesso")
    @ApiResponse(responseCode = "404", description = "Veículo não encontrado")
    public abstract ResponseEntity<Void> delete(@Parameter(description = "Identificador do veículo a ser excluído") @PathVariable @LicensePlate String licensePlate);

    @GetMapping("/{licensePlate}")
    @Operation(summary = "Buscar veículo", description = "Busca os detalhes de um veículo através da placa")
    @ApiResponse(responseCode = "200", description = "Veículo encontrado com sucesso")
    @ApiResponse(responseCode = "404", description = "Veículo não encontrado")
    public abstract ResponseEntity<VehicleResponse> findById(@Parameter(description = "Placa do veículo a ser buscado") @PathVariable @LicensePlate String licensePlate);

    @GetMapping
    @Operation(summary = "Listar veículos", description = "Lista veículos com paginação e filtros opcionais")
    @ApiResponse(responseCode = "200", description = "Lista recuperada com sucesso")
    public abstract ResponseEntity<Page<VehicleResponse>> getAll(
            @Parameter(description = "Filtro parcial por placa") @RequestParam(required = false) String licensePlate,
            @Parameter(description = "Filtro parcial por modelo") @RequestParam(required = false) String model,
            @Parameter(description = "Filtro equal por marca") @RequestParam(required = false) String brand,
            @Parameter(description = "Filtro equal por ano") @RequestParam(required = false) Short modelYear,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable);
}
