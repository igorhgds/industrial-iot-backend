package henrique.igor.iiot.infrastructure.web.controllers;

import henrique.igor.iiot.application.usecases.sensor.ListSensorByEquipmentUseCase;
import henrique.igor.iiot.application.usecases.sensor.RegisterSensorUseCase;
import henrique.igor.iiot.application.usecases.sensor.UpdateSensorUseCase;
import henrique.igor.iiot.domain.entities.Sensor;
import henrique.igor.iiot.infrastructure.web.dtos.sensor.RegisterSensorRequest;
import henrique.igor.iiot.infrastructure.web.dtos.sensor.SensorResponse;
import henrique.igor.iiot.infrastructure.web.dtos.sensor.UpdateSensorRequest;
import henrique.igor.iiot.infrastructure.web.specs.SensorControllerSpec;
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
@RequestMapping("/api/v1/sensors")
public class SensorController implements SensorControllerSpec {

    private final RegisterSensorUseCase registerSensorUseCase;
    private final ListSensorByEquipmentUseCase listSensorByEquipmentUseCase;
    private final UpdateSensorUseCase updateSensorUseCase;

    public SensorController(
            RegisterSensorUseCase registerSensorUseCase,
            ListSensorByEquipmentUseCase listSensorByEquipmentUseCase,
            UpdateSensorUseCase updateSensorUseCase
    ) {
        this.registerSensorUseCase = registerSensorUseCase;
        this.listSensorByEquipmentUseCase = listSensorByEquipmentUseCase;
        this.updateSensorUseCase = updateSensorUseCase;
    }

    @Override
    @PostMapping
    public ResponseEntity<SensorResponse> register(@Valid @RequestBody RegisterSensorRequest request) {
        Sensor registeredSensor = registerSensorUseCase.execute(request.toInput());
        return ResponseEntity.status(HttpStatus.CREATED).body(SensorResponse.fromDomain(registeredSensor));
    }

    @Override
    @GetMapping("/equipment/{equipmentId}")
    public ResponseEntity<List<SensorResponse>> listByEquipment(@PathVariable UUID equipmentId) {
        List<SensorResponse> sensors = listSensorByEquipmentUseCase.execute(equipmentId).stream()
                .map(SensorResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(sensors);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<SensorResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSensorRequest request
    ) {
        Sensor updatedSensor = updateSensorUseCase.execute(request.toInput(id));
        return ResponseEntity.ok(SensorResponse.fromDomain(updatedSensor));
    }
}
