package com.mecanicadm.workorder_api.infra.features.client.api;

import com.mecanicadm.workorder_api.core.client.usecase.CreateClientUseCase;
import com.mecanicadm.workorder_api.core.client.usecase.GetAllClientUseCase;
import com.mecanicadm.workorder_api.core.client.usecase.SoftDeleteClientUseCase;
import com.mecanicadm.workorder_api.core.client.usecase.UpdateClientUseCase;
import com.mecanicadm.workorder_api.core.client.usecase.command.CreateClientCommand;
import com.mecanicadm.workorder_api.core.client.usecase.command.SoftDeleteClientCommand;
import com.mecanicadm.workorder_api.infra.features.client.api.dto.request.CreateClientRequest;
import com.mecanicadm.workorder_api.infra.features.client.api.dto.request.UpdateClientRequest;
import com.mecanicadm.workorder_api.infra.features.client.api.dto.response.ClientResponse;
import com.mecanicadm.workorder_api.infra.features.client.api.openapi.ClientOpenApi;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/clients")
public class ClientController implements ClientOpenApi {

    private final CreateClientUseCase createClientUseCase;
    private final GetAllClientUseCase getAllClientUseCase;
    private final UpdateClientUseCase updateClientUseCase;
    private final SoftDeleteClientUseCase softDeleteClientUseCase;

    public ClientController(CreateClientUseCase createClientUseCase,
                            GetAllClientUseCase getAllClientUseCase,
                            UpdateClientUseCase updateClientUseCase,
                            SoftDeleteClientUseCase softDeleteClientUseCase) {
        this.createClientUseCase = createClientUseCase;
        this.getAllClientUseCase = getAllClientUseCase;
        this.updateClientUseCase = updateClientUseCase;
        this.softDeleteClientUseCase = softDeleteClientUseCase;
    }

    @Override
    @PostMapping
    public ResponseEntity<UUID> create(@RequestBody @Valid CreateClientRequest request) {
        var command = new CreateClientCommand(request.name(), request.email(), request.document(), request.phone());
        UUID clientId = createClientUseCase.execute(command);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(clientId).toUri();
        return ResponseEntity.created(uri).body(clientId);
    }

    @Override
    @GetMapping
    public ResponseEntity<Page<ClientResponse>> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String document,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        var query = ClientQueryMapper.toQuery(name, document, pageable);
        var result = getAllClientUseCase.execute(query);
        return ResponseEntity.ok(ClientQueryMapper.toPage(result, pageable));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody @Valid UpdateClientRequest request) {
        var command = request.withId(id);
        updateClientUseCase.execute(command);
        return ResponseEntity.ok().build();
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        softDeleteClientUseCase.execute(new SoftDeleteClientCommand(id));
        return ResponseEntity.noContent().build();
    }
}
