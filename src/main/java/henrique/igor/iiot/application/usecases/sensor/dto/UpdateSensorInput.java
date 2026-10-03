package henrique.igor.iiot.application.usecases.sensor.dto;

import henrique.igor.iiot.domain.entities.enums.SensorStatus;

import java.util.UUID;

public record UpdateSensorInput(
        UUID sensorId,
        SensorStatus status,
        String mqttTopic,
        UUID equipmentId
) {
}
