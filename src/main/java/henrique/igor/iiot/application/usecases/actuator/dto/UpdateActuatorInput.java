package henrique.igor.iiot.application.usecases.actuator.dto;

import henrique.igor.iiot.domain.entities.enums.ActuatorStatus;

import java.util.UUID;

public record UpdateActuatorInput(
        UUID actuatorId,
        ActuatorStatus actuatorStatus,
        String commandMqttTopic,
        String stateMqttTopic,
        UUID equipmentId
) {
}
