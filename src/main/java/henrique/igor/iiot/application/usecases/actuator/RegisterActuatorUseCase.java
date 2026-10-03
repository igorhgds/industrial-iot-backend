package henrique.igor.iiot.application.usecases.actuator;

import henrique.igor.iiot.application.usecases.actuator.dto.RegisterActuatorInput;
import henrique.igor.iiot.domain.entities.Actuator;
import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.repositories.ActuatorRepository;
import henrique.igor.iiot.domain.repositories.EquipmentRepository;
import henrique.igor.iiot.domain.services.ActuatorCodeGenerator;

public class RegisterActuatorUseCase {

    private final ActuatorRepository actuatorRepository;
    private final EquipmentRepository equipmentRepository;
    private final ActuatorCodeGenerator actuatorCodeGenerator;

    public RegisterActuatorUseCase(ActuatorRepository actuatorRepository, EquipmentRepository equipmentRepository, ActuatorCodeGenerator actuatorCodeGenerator) {
        this.actuatorRepository = actuatorRepository;
        this.equipmentRepository = equipmentRepository;
        this.actuatorCodeGenerator = actuatorCodeGenerator;
    }

    public Actuator execute(RegisterActuatorInput input){
        if(input.type() == null){
            throw new IllegalArgumentException("Actuator type cannot be null.");
        }

        Equipment equipment = null;
        if (input.equipmentId() != null){
            equipment = equipmentRepository.findById(input.equipmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Equipment not found with ID: " + input.equipmentId()));
        }

        String actuatorCode = input.code();
        if (actuatorCode == null || actuatorCode.isBlank()){
            actuatorCode = actuatorCodeGenerator.generate(input.type(), equipment);
        } else {
            if (actuatorRepository.findByCode(actuatorCode).isPresent()){
                throw new IllegalArgumentException("Actuator with code " + actuatorCode + " already exists.");
            }
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
}
