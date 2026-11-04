package henrique.igor.iiot.infrastructure.web.controllers;

import henrique.igor.iiot.application.usecases.actuator.RegisterActuatorUseCase;
import henrique.igor.iiot.application.usecases.actuator.UpdateActuatorUseCase;
import henrique.igor.iiot.domain.entities.Actuator;
import henrique.igor.iiot.infrastructure.web.dtos.actuator.ActuatorResponse;
import henrique.igor.iiot.infrastructure.web.dtos.actuator.RegisterActuatorRequest;
import henrique.igor.iiot.infrastructure.web.dtos.actuator.UpdateActuatorRequest;
import henrique.igor.iiot.infrastructure.web.specs.ActuatorControllerSpec;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/actuators")
public class ActuatorController implements ActuatorControllerSpec {

    private final RegisterActuatorUseCase registerActuatorUseCase;
    private final UpdateActuatorUseCase updateActuatorUseCase;

    public ActuatorController(
            RegisterActuatorUseCase registerActuatorUseCase,
            UpdateActuatorUseCase updateActuatorUseCase
    ) {
        this.registerActuatorUseCase = registerActuatorUseCase;
        this.updateActuatorUseCase = updateActuatorUseCase;
    }

    @Override
    @PostMapping
    public ResponseEntity<ActuatorResponse> register(@Valid @RequestBody RegisterActuatorRequest request) {
        Actuator registeredActuator = registerActuatorUseCase.execute(request.toInput());
        return ResponseEntity.status(HttpStatus.CREATED).body(ActuatorResponse.fromDomain(registeredActuator));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<ActuatorResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateActuatorRequest request
    ) {
        Actuator updatedActuator = updateActuatorUseCase.execute(request.toInput(id));
        return ResponseEntity.ok(ActuatorResponse.fromDomain(updatedActuator));
    }
}
