package henrique.igor.iiot.application.usecases.sensor;

import henrique.igor.iiot.application.usecases.sensor.dto.UpdateSensorRequest;
import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.Sensor;
import henrique.igor.iiot.domain.repositories.EquipmentRepository;
import henrique.igor.iiot.domain.repositories.SensorRepository;
import henrique.igor.iiot.domain.services.SensorCodeGenerator;

import java.util.Objects;
import java.util.UUID;

public class UpdateSensorUseCase {

    private final SensorRepository sensorRepository;
    private final EquipmentRepository equipmentRepository;
    private final SensorCodeGenerator sensorCodeGenerator;

    public UpdateSensorUseCase(SensorRepository sensorRepository,
                               EquipmentRepository equipmentRepository,
                               SensorCodeGenerator sensorCodeGenerator) {
        this.sensorRepository = sensorRepository;
        this.equipmentRepository = equipmentRepository;
        this.sensorCodeGenerator = sensorCodeGenerator;
    }

    public Sensor execute(UpdateSensorRequest request) {
        Sensor sensor = sensorRepository.findById(request.sensorId())
                .orElseThrow(() -> new IllegalArgumentException("Sensor not found with ID: " + request.sensorId()));

        if (request.status() != null) {
            sensor.changeStatus(request.status());
        }

        if (request.mqttTopic() != null) {
            sensor.updateMqttTopic(request.mqttTopic());
        }

        if (request.equipmentId() != null) {
            UUID currentEquipId = sensor.getEquipment() != null ? sensor.getEquipment().getEquipmentId() : null;
            if (!Objects.equals(currentEquipId, request.equipmentId())) {
                Equipment newEquipment = equipmentRepository.findById(request.equipmentId())
                        .orElseThrow(() -> new IllegalArgumentException("Equipment not found with ID: " + request.equipmentId()));

                String newCode = sensorCodeGenerator.generate(sensor.getSensorType(), newEquipment);
                sensor.relocateToEquipment(newEquipment, newCode);
            }
        }

        return sensorRepository.save(sensor);
    }
}
