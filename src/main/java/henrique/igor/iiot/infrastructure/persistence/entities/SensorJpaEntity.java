package henrique.igor.iiot.infrastructure.persistence.entities;

import henrique.igor.iiot.domain.entities.enums.SensorStatus;
import henrique.igor.iiot.domain.entities.enums.SensorType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sensors")
@Getter
@Setter
public class SensorJpaEntity {

    @Id
    private UUID sensorId;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SensorType sensorType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SensorStatus status;

    private String unitOfMeasure;

    private BigDecimal minThreshold;

    private BigDecimal maxThreshold;

    private String mqttTopic;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id")
    private EquipmentJpaEntity equipment;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public SensorJpaEntity(){}

    public SensorJpaEntity(UUID sensorId, String code, SensorType sensorType, SensorStatus status, String unitOfMeasure, BigDecimal minThreshold, BigDecimal maxThreshold, String mqttTopic, EquipmentJpaEntity equipment, LocalDateTime createdAt) {
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
}
