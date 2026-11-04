package henrique.igor.iiot.infrastructure.web.controllers;

import henrique.igor.iiot.application.usecases.user.CreateUserUseCase;
import henrique.igor.iiot.application.usecases.user.GetUserByIdUseCase;
import henrique.igor.iiot.application.usecases.user.ListUserUseCase;
import henrique.igor.iiot.application.usecases.user.UpdateUserUseCase;
import henrique.igor.iiot.domain.entities.User;
import henrique.igor.iiot.infrastructure.web.dtos.user.CreateUserRequest;
import henrique.igor.iiot.infrastructure.web.dtos.user.UpdateUserRequest;
import henrique.igor.iiot.infrastructure.web.dtos.user.UserResponse;
import henrique.igor.iiot.infrastructure.web.specs.UserControllerSpec;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController implements UserControllerSpec {

    private final CreateUserUseCase createUserUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final ListUserUseCase listUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;

    public UserController(
            CreateUserUseCase createUserUseCase,
            GetUserByIdUseCase getUserByIdUseCase,
            ListUserUseCase listUserUseCase,
            UpdateUserUseCase updateUserUseCase
    ) {
        this.createUserUseCase = createUserUseCase;
        this.getUserByIdUseCase = getUserByIdUseCase;
        this.listUserUseCase = listUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
    }

    @Override
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        User createdUser = createUserUseCase.execute(request.toInput());
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.fromDomain(createdUser));
    }

    @Override
    @GetMapping
    public ResponseEntity<List<UserResponse>> listAll() {
        List<UserResponse> users = listUserUseCase.execute().stream()
                .map(UserResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(users);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable UUID id) {
        User user = getUserByIdUseCase.execute(id);
        return ResponseEntity.ok(UserResponse.fromDomain(user));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        User updatedUser = updateUserUseCase.execute(request.toInput(id));
        return ResponseEntity.ok(UserResponse.fromDomain(updatedUser));
    }
}
