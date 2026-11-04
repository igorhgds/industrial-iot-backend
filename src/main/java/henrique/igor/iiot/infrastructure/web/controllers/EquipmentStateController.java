package henrique.igor.iiot.infrastructure.web.controllers;

import henrique.igor.iiot.application.usecases.equipmentState.GetEquipmentStateUseCase;
import henrique.igor.iiot.application.usecases.equipmentState.ListEquipmentStatesUseCase;
import henrique.igor.iiot.domain.entities.EquipmentState;
import henrique.igor.iiot.infrastructure.web.dtos.equipmentState.EquipmentStateResponse;
import henrique.igor.iiot.infrastructure.web.specs.EquipmentStateControllerSpec;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/equipment-states")
public class EquipmentStateController implements EquipmentStateControllerSpec {

    private final ListEquipmentStatesUseCase listEquipmentStatesUseCase;
    private final GetEquipmentStateUseCase getEquipmentStateUseCase;

    public EquipmentStateController(
            ListEquipmentStatesUseCase listEquipmentStatesUseCase,
            GetEquipmentStateUseCase getEquipmentStateUseCase
    ) {
        this.listEquipmentStatesUseCase = listEquipmentStatesUseCase;
        this.getEquipmentStateUseCase = getEquipmentStateUseCase;
    }

    @Override
    @GetMapping
    public ResponseEntity<List<EquipmentStateResponse>> listAll() {
        List<EquipmentStateResponse> states = listEquipmentStatesUseCase.execute().stream()
                .map(EquipmentStateResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(states);
    }

    @Override
    @GetMapping("/{equipmentId}")
    public ResponseEntity<EquipmentStateResponse> getByEquipmentId(@PathVariable UUID equipmentId) {
        EquipmentState state = getEquipmentStateUseCase.execute(equipmentId);
        return ResponseEntity.ok(EquipmentStateResponse.fromDomain(state));
    }
}
