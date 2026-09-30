package henrique.igor.iiot.application.usecases.sensor.dto;

import henrique.igor.iiot.domain.entities.enums.SensorStatus;
import henrique.igor.iiot.domain.entities.enums.SensorType;

import java.util.UUID;

public record RegisterSensorRequest(
    String code,
    SensorType sensorType,
    SensorStatus status,
    String unitOfMeasure,
    String mqttTopic,
    UUID equipmentId
) {
}
