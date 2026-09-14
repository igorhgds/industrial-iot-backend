package henrique.igor.iiot.domain.entities;

import henrique.igor.iiot.domain.entities.enums.SensorStatus;
import henrique.igor.iiot.domain.entities.enums.SensorType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class Sensor {

    private final UUID sensorId;
    private final String code;
    private final SensorType sensorType;
    private SensorStatus status;
    private final String unitOfMeasure;
    private String mqttTopic;
    private Equipment equipment;
    private final OffsetDateTime createdAt;

    public Sensor(String code, SensorType sensorType, String unitOfMeasure, String mqttTopic, Equipment equipment) {
        this(UUID.randomUUID(), code , sensorType, SensorStatus.ONLINE, unitOfMeasure, mqttTopic, equipment, OffsetDateTime.now());
    }

    public Sensor(UUID sensorId, String code, SensorType sensorType, SensorStatus status, String unitOfMeasure, String mqttTopic, Equipment equipment, OffsetDateTime createdAt) {
        this.sensorId = sensorId;
        this.code = code;
        this.sensorType = sensorType;
        this.status = status;
        this.unitOfMeasure = unitOfMeasure;
        this.mqttTopic = mqttTopic;
        this.equipment = equipment;
        this.createdAt = createdAt;
    }

    public UUID getSensorId() {return sensorId;}
    public String getCode() {return code;}
    public SensorType getSensorType() {return sensorType;}
    public SensorStatus getStatus() {return status;}
    public String getUnitOfMeasure() {return unitOfMeasure;}
    public String getMqttTopic() {return mqttTopic;}
    public Equipment getEquipment() {return equipment;}
    public OffsetDateTime getCreatedAt() {return createdAt;}
}
