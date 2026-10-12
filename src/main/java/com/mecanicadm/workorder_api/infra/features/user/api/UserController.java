package com.mecanicadm.workorder_api.infra.features.user.api;

import com.mecanicadm.workorder_api.core.user.usecase.*;
import com.mecanicadm.workorder_api.core.user.usecase.command.*;
import com.mecanicadm.workorder_api.core.user.usecase.query.AuthenticateUserQuery;
import com.mecanicadm.workorder_api.core.user.usecase.query.FindUserByIdQuery;
import com.mecanicadm.workorder_api.infra.features.user.api.dto.*;
import com.mecanicadm.workorder_api.infra.features.user.api.openapi.UserOpenApi;
import com.mecanicadm.workorder_api.infra.security.UserAdapter;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController implements UserOpenApi {

    private final CreateUserUseCase createUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final SoftDeleteUserUseCase softDeleteUserUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final RequestPasswordResetUseCase requestPasswordResetUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;

    public UserController(CreateUserUseCase createUserUseCase,
                          UpdateUserUseCase updateUserUseCase,
                          AuthenticateUserUseCase authenticateUserUseCase,
                          SoftDeleteUserUseCase softDeleteUserUseCase,
                          GetUserByIdUseCase getUserByIdUseCase,
                          RequestPasswordResetUseCase requestPasswordResetUseCase,
                          ResetPasswordUseCase resetPasswordUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.authenticateUserUseCase = authenticateUserUseCase;
        this.softDeleteUserUseCase = softDeleteUserUseCase;
        this.getUserByIdUseCase = getUserByIdUseCase;
        this.requestPasswordResetUseCase = requestPasswordResetUseCase;
        this.resetPasswordUseCase = resetPasswordUseCase;
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid LoginRequest request) {
        var query = new AuthenticateUserQuery(request.email(), request.password());
        var response = AuthenticationResponse.from(authenticateUserUseCase.execute(query));
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        requestPasswordResetUseCase.execute(new RequestPasswordResetCommand(request.email()));
        return ResponseEntity.ok().build();
    }

    @Override
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        resetPasswordUseCase.execute(new ResetPasswordCommand(request.token(), request.newPassword()));
        return ResponseEntity.ok().build();
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findUser(@PathVariable UUID id) {
        var user = getUserByIdUseCase.execute(new FindUserByIdQuery(id));
        return ResponseEntity.ok(UserResponse.fromEntity(user));
    }

    @Override
    @PostMapping
    public ResponseEntity<UUID> create(@RequestBody @Valid CreateUserRequest request) {
        var command = new CreateUserCommand(request.email(), request.password(), request.name());
        UUID userId = createUserUseCase.execute(command);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(userId).toUri();
        return ResponseEntity.created(uri).body(userId);
    }

    @Override
    @PutMapping
    public ResponseEntity<Void> update(@RequestBody @Valid UpdateUserRequest request, @AuthenticationPrincipal UserAdapter userAdapter) {
        var command = new UpdateUserCommand(userAdapter.user().getId(), request.name(), request.password(), request.currentPassword());
        updateUserUseCase.execute(command);
        return ResponseEntity.ok().build();
    }

    @Override
    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal UserAdapter userAdapter) {
        softDeleteUserUseCase.execute(new SoftDeleteUserCommand(userAdapter.user().getId()));
        return ResponseEntity.noContent().build();
    }
}
