package henrique.igor.iiot.application.usecases.actuator;

import henrique.igor.iiot.application.usecases.actuator.dto.RegisterActuatorInput;
import henrique.igor.iiot.domain.entities.Actuator;
import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.enums.ActuatorType;
import henrique.igor.iiot.domain.exceptions.*;
import henrique.igor.iiot.domain.repositories.ActuatorRepository;
import henrique.igor.iiot.domain.repositories.EquipmentRepository;
import henrique.igor.iiot.domain.services.ActuatorCodeGenerator;

import java.util.UUID;

public class RegisterActuatorUseCase {

    private final ActuatorRepository actuatorRepository;
    private final EquipmentRepository equipmentRepository;
    private final ActuatorCodeGenerator actuatorCodeGenerator;

    public RegisterActuatorUseCase(
            ActuatorRepository actuatorRepository,
            EquipmentRepository equipmentRepository,
            ActuatorCodeGenerator actuatorCodeGenerator
    ) {
        this.actuatorRepository = actuatorRepository;
        this.equipmentRepository = equipmentRepository;
        this.actuatorCodeGenerator = actuatorCodeGenerator;
    }

    public Actuator execute(RegisterActuatorInput input) {
        if (input == null) {
            throw new DomainValidationException("Input payload cannot be null.");
        }
        if (input.type() == null) {
            throw new DomainValidationException("Actuator type cannot be null.");
        }

        Equipment equipment = resolveEquipment(input.equipmentId());
        String actuatorCode = resolveActuatorCode(input.code(), input.type(), equipment);

        if (actuatorRepository.findByCode(actuatorCode).isPresent()) {
            throw new ResourceAlreadyExistsException("Actuator", "code", actuatorCode);
        }

        Actuator newActuator = new Actuator(
                actuatorCode,
                input.type(),
                input.commandMqttTopic(),
                input.stateMqttTopic(),
                equipment
        );

        return actuatorRepository.save(newActuator);
    }

    private Equipment resolveEquipment(UUID equipmentId) {
        if (equipmentId == null) {
            return null;
        }
        return equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", equipmentId));
    }

    private String resolveActuatorCode(String providedCode, ActuatorType type, Equipment equipment) {
        if (providedCode != null && !providedCode.isBlank()) {
            return providedCode.trim();
        }
        return actuatorCodeGenerator.generate(type, equipment);
    }
}