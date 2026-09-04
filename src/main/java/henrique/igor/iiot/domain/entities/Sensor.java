package henrique.igor.iiot.domain.entities;

import henrique.igor.iiot.domain.entities.enums.SensorStatus;
import henrique.igor.iiot.domain.entities.enums.SensorType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Sensor {

    private final UUID sensorId;
    private final String code;
    private final SensorType sensorType;
    private SensorStatus status;
    private final String unitOfMeasure;
    private BigDecimal minThreshold;
    private BigDecimal maxThreshold;
    private String mqttTopic;
    private Equipment equipment;
    private final LocalDateTime createdAt;

    public Sensor(String code, SensorType sensorType, SensorStatus status, String unitOfMeasure, BigDecimal minThreshold, BigDecimal maxThreshold, String mqttTopic, Equipment equipment) {
        this(UUID.randomUUID(), code , sensorType, status, unitOfMeasure, minThreshold, maxThreshold, mqttTopic, equipment, LocalDateTime.now());
    }

    public Sensor(UUID sensorId, String code, SensorType sensorType, SensorStatus status, String unitOfMeasure, BigDecimal minThreshold, BigDecimal maxThreshold, String mqttTopic, Equipment equipment, LocalDateTime createdAt) {
        this.sensorId = sensorId;
        this.code = code;
        this.sensorType = sensorType;
        this.status = status;
        this.unitOfMeasure = unitOfMeasure;
        this.minThreshold = minThreshold;
        this.maxThreshold = maxThreshold;
        this.mqttTopic = mqttTopic;
        this.equipment = equipment;
        this.createdAt = createdAt;
    }

    public UUID getSensorId() {return sensorId;}
    public String getCode() {return code;}
    public SensorType getSensorType() {return sensorType;}
    public SensorStatus getStatus() {return status;}
    public String getUnitOfMeasure() {return unitOfMeasure;}
    public BigDecimal getMinThreshold() {return minThreshold;}
    public BigDecimal getMaxThreshold() {return maxThreshold;}
    public String getMqttTopic() {return mqttTopic;}
    public Equipment getEquipment() {return equipment;}
    public LocalDateTime getCreatedAt() {return createdAt;}
}
