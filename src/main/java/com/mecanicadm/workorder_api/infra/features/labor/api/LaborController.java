package com.mecanicadm.workorder_api.infra.features.labor.api;

import com.mecanicadm.workorder_api.core.labor.usecase.CreateLaborUseCase;
import com.mecanicadm.workorder_api.core.labor.usecase.DeleteLaborUseCase;
import com.mecanicadm.workorder_api.core.labor.usecase.GetAllLaborsUseCase;
import com.mecanicadm.workorder_api.core.labor.usecase.GetLaborByIdUseCase;
import com.mecanicadm.workorder_api.core.labor.usecase.UpdateLaborUseCase;
import com.mecanicadm.workorder_api.core.labor.usecase.command.CreateLaborCommand;
import com.mecanicadm.workorder_api.core.labor.usecase.command.DeleteLaborCommand;
import com.mecanicadm.workorder_api.core.labor.usecase.command.UpdateLaborCommand;
import com.mecanicadm.workorder_api.core.labor.usecase.query.GetLaborByIdQuery;
import com.mecanicadm.workorder_api.infra.features.labor.api.dto.request.CreateLaborRequest;
import com.mecanicadm.workorder_api.infra.features.labor.api.dto.request.UpdateLaborRequest;
import com.mecanicadm.workorder_api.infra.features.labor.api.dto.response.LaborResponse;
import com.mecanicadm.workorder_api.infra.features.labor.api.openapi.LaborOpenApi;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/labor")
public class LaborController implements LaborOpenApi {

    private final CreateLaborUseCase createLaborUseCase;
    private final UpdateLaborUseCase updateLaborUseCase;
    private final DeleteLaborUseCase deleteLaborUseCase;
    private final GetLaborByIdUseCase getLaborByIdUseCase;
    private final GetAllLaborsUseCase getAllLaborsUseCase;

    public LaborController(CreateLaborUseCase createLaborUseCase,
                           UpdateLaborUseCase updateLaborUseCase,
                           DeleteLaborUseCase deleteLaborUseCase,
                           GetLaborByIdUseCase getLaborByIdUseCase,
                           GetAllLaborsUseCase getAllLaborsUseCase) {
        this.createLaborUseCase = createLaborUseCase;
        this.updateLaborUseCase = updateLaborUseCase;
        this.deleteLaborUseCase = deleteLaborUseCase;
        this.getLaborByIdUseCase = getLaborByIdUseCase;
        this.getAllLaborsUseCase = getAllLaborsUseCase;
    }

    @Override
    @PostMapping
    public ResponseEntity<UUID> create(@Valid @RequestBody CreateLaborRequest request) {
        UUID id = createLaborUseCase.execute(new CreateLaborCommand(request.name(), request.price()));
        return ResponseEntity.created(URI.create(String.format("/labor/%s", id))).body(id);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable UUID id, @Valid @RequestBody UpdateLaborRequest request) {
        updateLaborUseCase.execute(new UpdateLaborCommand(id, request.name(), request.price()));
        return ResponseEntity.noContent().build();
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteLaborUseCase.execute(new DeleteLaborCommand(id));
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<LaborResponse> findById(@PathVariable UUID id) {
        var labor = getLaborByIdUseCase.execute(new GetLaborByIdQuery(id));
        return ResponseEntity.ok(LaborResponse.from(labor));
    }

    @Override
    @GetMapping
    public ResponseEntity<Page<LaborResponse>> getAll(@RequestParam(required = false) String name,
                                                       @PageableDefault(size = 20) Pageable pageable) {
        var query = LaborQueryMapper.toQuery(name, pageable);
        var result = getAllLaborsUseCase.execute(query);
        return ResponseEntity.ok(LaborQueryMapper.toPage(result, pageable));
    }
}
