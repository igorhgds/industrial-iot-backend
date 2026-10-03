package henrique.igor.iiot.application.usecases.actuator;

import henrique.igor.iiot.application.usecases.actuator.dto.UpdateActuatorInput;
import henrique.igor.iiot.domain.entities.Actuator;
import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.repositories.ActuatorRepository;
import henrique.igor.iiot.domain.repositories.EquipmentRepository;
import henrique.igor.iiot.domain.services.ActuatorCodeGenerator;

import java.util.Objects;
import java.util.UUID;

public class UpdateActuatorUseCase {

    private final ActuatorRepository actuatorRepository;
    private final EquipmentRepository equipmentRepository;
    private final ActuatorCodeGenerator actuatorCodeGenerator;

    public UpdateActuatorUseCase(ActuatorRepository actuatorRepository, EquipmentRepository equipmentRepository, ActuatorCodeGenerator actuatorCodeGenerator) {
        this.actuatorRepository = actuatorRepository;
        this.equipmentRepository = equipmentRepository;
        this.actuatorCodeGenerator = actuatorCodeGenerator;
    }

    public Actuator execute(UpdateActuatorInput input){
        Actuator actuator = actuatorRepository.findById(input.actuatorId())
                .orElseThrow(() -> new IllegalArgumentException("Actuator not found with ID: " + input.actuatorId()));

        if (input.actuatorStatus() != null){
            actuator.changeStatus(input.actuatorStatus());
        }

        if (input.commandMqttTopic() != null){
            actuator.updateCommandMqttTopic(input.commandMqttTopic());
        }

        if (input.stateMqttTopic() != null){
            actuator.updateStateMqttTopic(input.stateMqttTopic());
        }

        if (input.equipmentId() != null){
            UUID currentEquipId = actuator.getEquipment() != null ? actuator.getEquipment().getEquipmentId() : null;
            if(!Objects.equals(currentEquipId, input.equipmentId())){
                Equipment newEquipment = equipmentRepository.findById(input.equipmentId())
                        .orElseThrow(() -> new IllegalArgumentException("Equipment not found with ID: " + input.equipmentId()));

                String newCode = actuatorCodeGenerator.generate(actuator.getActuatorType(), newEquipment);
                actuator.relocateToEquipment(newEquipment, newCode);
            }
        }

        return actuatorRepository.save(actuator);
    }
}
