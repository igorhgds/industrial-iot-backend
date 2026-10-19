package henrique.igor.iiot.application.usecases.sensor;

import henrique.igor.iiot.application.usecases.sensor.dto.RegisterSensorInput;
import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.Sensor;
import henrique.igor.iiot.domain.exceptions.*;
import henrique.igor.iiot.domain.repositories.EquipmentRepository;
import henrique.igor.iiot.domain.repositories.SensorRepository;
import henrique.igor.iiot.domain.services.SensorCodeGenerator;

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

    public Sensor execute(RegisterSensorInput input) {
        if (input.sensorType() == null) {
            throw new DomainValidationException("Sensor type cannot be null.");
        }

        Equipment equipment = null;
        if (input.equipmentId() != null) {
            equipment = equipmentRepository.findById(input.equipmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Equipment", input.equipmentId()));
        }

        String sensorCode = input.code();
        if (sensorCode == null || sensorCode.isBlank()) {
            sensorCode = sensorCodeGenerator.generate(input.sensorType(), equipment);
        } else {
            if (sensorRepository.findByCode(sensorCode).isPresent()) {
                throw new ResourceAlreadyExistsException("Sensor", "code", sensorCode);
            }
        }

        Sensor newSensor = new Sensor(
                sensorCode,
                input.sensorType(),
                input.unitOfMeasure(),
                input.mqttTopic(),
                equipment
        );

        return sensorRepository.save(newSensor);
    }
}
