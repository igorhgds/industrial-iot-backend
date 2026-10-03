package henrique.igor.iiot.domain.entities;

import henrique.igor.iiot.domain.entities.enums.ActuatorStatus;
import henrique.igor.iiot.domain.entities.enums.ActuatorType;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Actuator {
    private final UUID actuatorId;
    private String code;
    private final ActuatorType actuatorType;
    private ActuatorStatus status;
    private String commandMqttTopic;
    private String stateMqttTopic;
    private Equipment equipment;
    private final OffsetDateTime createdAt;

    public Actuator(String code, ActuatorType actuatorType, String commandMqttTopic, String stateMqttTopic, Equipment equipment) {
        this(UUID.randomUUID(), code, actuatorType, ActuatorStatus.OFFLINE, commandMqttTopic, stateMqttTopic, equipment, OffsetDateTime.now());
    }

    public Actuator(UUID actuatorId, String code, ActuatorType actuatorType, ActuatorStatus status, String commandMqttTopic, String stateMqttTopic, Equipment equipment, OffsetDateTime createdAt) {
        this.actuatorId = actuatorId;
        this.code = code;
        this.actuatorType = actuatorType;
        this.status = status;
        this.commandMqttTopic = commandMqttTopic;
        this.stateMqttTopic = stateMqttTopic;
        this.equipment = equipment;
        this.createdAt = createdAt;
    }

    public void changeStatus(ActuatorStatus newStatus){
        if(newStatus != null){
            this.status = newStatus;
        }
    }

    public void updateCommandMqttTopic(String newCommandMqttTopic) {
        if (newCommandMqttTopic != null && !newCommandMqttTopic.isBlank()) {
            this.commandMqttTopic = newCommandMqttTopic;
        }
    }

    public void updateStateMqttTopic(String newStateMqttTopic){
        if (newStateMqttTopic != null && !newStateMqttTopic.isBlank()){
            this.stateMqttTopic = newStateMqttTopic;
        }
    }

    public void relocateToEquipment(Equipment newEquipment, String newCode) {
        this.equipment = newEquipment;
        if (newCode != null && !newCode.isBlank()) {
            this.code = newCode;
        }
    }

    public UUID getActuatorId() {return actuatorId;}
    public String getCode() {return code;}
    public ActuatorType getActuatorType() {return actuatorType;}
    public ActuatorStatus getStatus() {return status;}
    public String getCommandMqttTopic() {return commandMqttTopic;}
    public String getStateMqttTopic() {return stateMqttTopic;}
    public Equipment getEquipment() {return equipment;}
    public OffsetDateTime getCreatedAt() {return createdAt;}
}
