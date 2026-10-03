package henrique.igor.iiot.application.usecases.sensor.dto;

import henrique.igor.iiot.domain.entities.enums.SensorType;

import java.util.UUID;

public record RegisterSensorInput(
    String code,
    SensorType sensorType,
    String unitOfMeasure,
    String mqttTopic,
    UUID equipmentId
) {
}
