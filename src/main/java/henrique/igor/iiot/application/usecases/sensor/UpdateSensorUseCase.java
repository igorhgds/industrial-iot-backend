package henrique.igor.iiot.application.usecases.sensor;

import henrique.igor.iiot.application.usecases.sensor.dto.UpdateSensorInput;
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

    public Sensor execute(UpdateSensorInput input) {
        Sensor sensor = sensorRepository.findById(input.sensorId())
                .orElseThrow(() -> new IllegalArgumentException("Sensor not found with ID: " + input.sensorId()));

        if (input.status() != null) {
            sensor.changeStatus(input.status());
        }

        if (input.mqttTopic() != null) {
            sensor.updateMqttTopic(input.mqttTopic());
        }

        if (input.equipmentId() != null) {
            UUID currentEquipId = sensor.getEquipment() != null ? sensor.getEquipment().getEquipmentId() : null;
            if (!Objects.equals(currentEquipId, input.equipmentId())) {
                Equipment newEquipment = equipmentRepository.findById(input.equipmentId())
                        .orElseThrow(() -> new IllegalArgumentException("Equipment not found with ID: " + input.equipmentId()));

                String newCode = sensorCodeGenerator.generate(sensor.getSensorType(), newEquipment);
                sensor.relocateToEquipment(newEquipment, newCode);
            }
        }

        return sensorRepository.save(sensor);
    }
}
