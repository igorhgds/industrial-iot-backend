package henrique.igor.iiot.infrastructure.web.controllers;

import henrique.igor.iiot.application.usecases.equipment.GetEquipmentDetailsUseCase;
import henrique.igor.iiot.application.usecases.equipment.RegisterEquipmentUseCase;
import henrique.igor.iiot.application.usecases.equipment.UpdateEquipmentUseCase;
import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.infrastructure.web.dtos.equipment.EquipmentResponse;
import henrique.igor.iiot.infrastructure.web.dtos.equipment.RegisterEquipmentRequest;
import henrique.igor.iiot.infrastructure.web.dtos.equipment.UpdateEquipmentRequest;
import henrique.igor.iiot.infrastructure.web.specs.EquipmentControllerSpec;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/equipments")
public class EquipmentController implements EquipmentControllerSpec {

    private final RegisterEquipmentUseCase registerEquipmentUseCase;
    private final GetEquipmentDetailsUseCase getEquipmentDetailsUseCase;
    private final UpdateEquipmentUseCase updateEquipmentUseCase;

    public EquipmentController(
            RegisterEquipmentUseCase registerEquipmentUseCase,
            GetEquipmentDetailsUseCase getEquipmentDetailsUseCase,
            UpdateEquipmentUseCase updateEquipmentUseCase
    ) {
        this.registerEquipmentUseCase = registerEquipmentUseCase;
        this.getEquipmentDetailsUseCase = getEquipmentDetailsUseCase;
        this.updateEquipmentUseCase = updateEquipmentUseCase;
    }

    @Override
    @PostMapping
    public ResponseEntity<EquipmentResponse> register(@Valid @RequestBody RegisterEquipmentRequest request) {
        Equipment registeredEquipment = registerEquipmentUseCase.execute(request.toInput());
        return ResponseEntity.status(HttpStatus.CREATED).body(EquipmentResponse.fromDomain(registeredEquipment));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<EquipmentResponse> getDetails(@PathVariable UUID id) {
        Equipment equipment = getEquipmentDetailsUseCase.execute(id);
        return ResponseEntity.ok(EquipmentResponse.fromDomain(equipment));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<EquipmentResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEquipmentRequest request
    ) {
        Equipment updatedEquipment = updateEquipmentUseCase.execute(request.toInput(id));
        return ResponseEntity.ok(EquipmentResponse.fromDomain(updatedEquipment));
    }
}
