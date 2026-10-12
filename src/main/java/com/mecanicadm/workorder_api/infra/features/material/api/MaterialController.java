package com.mecanicadm.workorder_api.infra.features.material.api;

import com.mecanicadm.workorder_api.core.material.domain.enums.MaterialType;
import com.mecanicadm.workorder_api.core.material.usecase.CreateMaterialUseCase;
import com.mecanicadm.workorder_api.core.material.usecase.GetAllMaterialsUseCase;
import com.mecanicadm.workorder_api.core.material.usecase.GetMaterialByIdUseCase;
import com.mecanicadm.workorder_api.core.material.usecase.SoftDeleteMaterialUseCase;
import com.mecanicadm.workorder_api.core.material.usecase.UpdateMaterialUseCase;
import com.mecanicadm.workorder_api.core.material.usecase.command.CreateMaterialCommand;
import com.mecanicadm.workorder_api.core.material.usecase.command.SoftDeleteMaterialCommand;
import com.mecanicadm.workorder_api.core.material.usecase.command.UpdateMaterialCommand;
import com.mecanicadm.workorder_api.core.material.usecase.query.GetMaterialByIdQuery;
import com.mecanicadm.workorder_api.infra.features.material.api.dto.request.CreateMaterialRequest;
import com.mecanicadm.workorder_api.infra.features.material.api.dto.request.UpdateMaterialRequest;
import com.mecanicadm.workorder_api.infra.features.material.api.dto.response.MaterialResponse;
import com.mecanicadm.workorder_api.infra.features.material.api.openapi.MaterialOpenApi;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/materials")
public class MaterialController implements MaterialOpenApi {

    private final CreateMaterialUseCase createMaterialUseCase;
    private final UpdateMaterialUseCase updateMaterialUseCase;
    private final GetMaterialByIdUseCase getMaterialByIdUseCase;
    private final GetAllMaterialsUseCase getAllMaterialsUseCase;
    private final SoftDeleteMaterialUseCase softDeleteMaterialUseCase;

    public MaterialController(CreateMaterialUseCase createMaterialUseCase,
                              UpdateMaterialUseCase updateMaterialUseCase,
                              GetMaterialByIdUseCase getMaterialByIdUseCase,
                              GetAllMaterialsUseCase getAllMaterialsUseCase,
                              SoftDeleteMaterialUseCase softDeleteMaterialUseCase) {
        this.createMaterialUseCase = createMaterialUseCase;
        this.updateMaterialUseCase = updateMaterialUseCase;
        this.getMaterialByIdUseCase = getMaterialByIdUseCase;
        this.getAllMaterialsUseCase = getAllMaterialsUseCase;
        this.softDeleteMaterialUseCase = softDeleteMaterialUseCase;
    }

    @Override
    @PostMapping
    public ResponseEntity<UUID> create(@Valid @RequestBody CreateMaterialRequest request) {
        UUID materialId = createMaterialUseCase.execute(new CreateMaterialCommand(request.name(), request.brand(),
                request.description(), request.price(), request.type(), request.quantity()));
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(materialId).toUri();
        return ResponseEntity.created(uri).body(materialId);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable UUID id, @Valid @RequestBody UpdateMaterialRequest request) {
        updateMaterialUseCase.execute(new UpdateMaterialCommand(id, request.name(), request.brand(),
                request.description(), request.price(), request.type()));
        return ResponseEntity.ok().build();
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<MaterialResponse> findById(@PathVariable UUID id) {
        var material = getMaterialByIdUseCase.execute(new GetMaterialByIdQuery(id));
        return ResponseEntity.ok(MaterialResponse.from(material));
    }

    @Override
    @GetMapping
    public ResponseEntity<Page<MaterialResponse>> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) MaterialType type,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        var query = MaterialQueryMapper.toQuery(name, brand, type, pageable);
        var result = getAllMaterialsUseCase.execute(query);
        return ResponseEntity.ok(MaterialQueryMapper.toPage(result, pageable));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        softDeleteMaterialUseCase.execute(new SoftDeleteMaterialCommand(id));
        return ResponseEntity.noContent().build();
    }
}
