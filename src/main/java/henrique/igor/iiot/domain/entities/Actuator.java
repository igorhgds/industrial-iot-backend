package henrique.igor.iiot.domain.entities;

import henrique.igor.iiot.domain.entities.enums.ActuatorStatus;
import henrique.igor.iiot.domain.entities.enums.ActuatorType;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Actuator {
    private final UUID actuatorId;
    private final String code;
    private ActuatorType actuatorType;
    private ActuatorStatus status;
    private String commandMqttTopic;
    private String stateMqttTopic;
    private Equipment equipment;
    private final OffsetDateTime createdAt;

    public Actuator(String code, ActuatorType actuatorType, ActuatorStatus status, String commandMqttTopic, String stateMqttTopic, Equipment equipment) {
        this(UUID.randomUUID(), code, actuatorType, status, commandMqttTopic, stateMqttTopic, equipment, OffsetDateTime.now());
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

    public UUID getActuatorId() {return actuatorId;}
    public String getCode() {return code;}
    public ActuatorType getActuatorType() {return actuatorType;}
    public ActuatorStatus getStatus() {return status;}
    public String getCommandMqttTopic() {return commandMqttTopic;}
    public String getStateMqttTopic() {return stateMqttTopic;}
    public Equipment getEquipment() {return equipment;}
    public OffsetDateTime getCreatedAt() {return createdAt;}
}
