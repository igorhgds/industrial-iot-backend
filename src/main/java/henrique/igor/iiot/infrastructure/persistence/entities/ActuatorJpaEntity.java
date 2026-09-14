package henrique.igor.iiot.infrastructure.persistence.entities;

import henrique.igor.iiot.domain.entities.enums.ActuatorStatus;
import henrique.igor.iiot.domain.entities.enums.ActuatorType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "actuators")
@Getter
@Setter
public class ActuatorJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID actuatorId;

    @Column(nullable = false, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActuatorType actuatorType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActuatorStatus status;

    private String commandMqttTopic;

    private String stateMqttTopic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id")
    private EquipmentJpaEntity equipment;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public ActuatorJpaEntity() {}

    public ActuatorJpaEntity(UUID actuatorId, String code, ActuatorType actuatorType, ActuatorStatus status, String commandMqttTopic, String stateMqttTopic, EquipmentJpaEntity equipment, OffsetDateTime createdAt) {
        this.actuatorId = actuatorId;
        this.code = code;
        this.actuatorType = actuatorType;
        this.status = status;
        this.commandMqttTopic = commandMqttTopic;
        this.stateMqttTopic = stateMqttTopic;
        this.equipment = equipment;
        this.createdAt = createdAt;
    }
}