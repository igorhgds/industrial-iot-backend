package henrique.igor.iiot.application.usecases.sensor;

import henrique.igor.iiot.application.usecases.sensor.dto.RegisterSensorRequest;
import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.Sensor;
import henrique.igor.iiot.domain.entities.enums.SensorStatus;
import henrique.igor.iiot.domain.repositories.EquipmentRepository;
import henrique.igor.iiot.domain.repositories.SensorRepository;
import henrique.igor.iiot.domain.services.SensorCodeGenerator;

import java.time.OffsetDateTime;
import java.util.UUID;

public class RegisterSensorUseCase {

    private final SensorRepository sensorRepository;
    private final EquipmentRepository equipmentRepository;
    private final SensorCodeGenerator sensorCodeGenerator;

    public RegisterSensorUseCase(SensorRepository sensorRepository,
                               EquipmentRepository equipmentRepository,
                               SensorCodeGenerator sensorCodeGenerator) {
        this.sensorRepository = sensorRepository;
        this.equipmentRepository = equipmentRepository;
        this.sensorCodeGenerator = sensorCodeGenerator;
    }

    public Sensor execute(RegisterSensorRequest request) {
        if (request.sensorType() == null) {
            throw new IllegalArgumentException("Sensor type cannot be null.");
        }

        Equipment equipment = null;
        if (request.equipmentId() != null) {
            equipment = equipmentRepository.findById(request.equipmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Equipment not found with ID: " + request.equipmentId()));
        }

        String sensorCode = request.code();
        if (sensorCode == null || sensorCode.isBlank()) {
            sensorCode = sensorCodeGenerator.generate(request.sensorType(), equipment);
        } else {
            if (sensorRepository.findByCode(sensorCode).isPresent()) {
                throw new IllegalArgumentException("Sensor with code " + sensorCode + " already exists.");
            }
        }

        SensorStatus status = request.status() != null ? request.status() : SensorStatus.ONLINE;

        Sensor newSensor = new Sensor(
                UUID.randomUUID(),
                sensorCode,
                request.sensorType(),
                status,
                request.unitOfMeasure(),
                request.mqttTopic(),
                equipment,
                OffsetDateTime.now()
        );

        return sensorRepository.save(newSensor);
    }
}
