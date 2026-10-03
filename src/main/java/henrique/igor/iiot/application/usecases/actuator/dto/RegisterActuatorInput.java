package henrique.igor.iiot.application.usecases.actuator.dto;

import henrique.igor.iiot.domain.entities.enums.ActuatorType;

import java.util.UUID;

public record RegisterActuatorInput(
        String code,
        ActuatorType type,
        String commandMqttTopic,
        String stateMqttTopic,
        UUID equipmentId
) {
}
