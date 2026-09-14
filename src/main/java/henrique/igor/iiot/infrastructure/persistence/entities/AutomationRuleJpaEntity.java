package henrique.igor.iiot.infrastructure.persistence.entities;

import henrique.igor.iiot.domain.entities.enums.ActionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "automation_rules")
@Getter
@Setter
public class AutomationRuleJpaEntity {

    @Id
    private UUID automationId;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "input_sensor_id")
    private SensorJpaEntity inputSensor;

    @ManyToOne
    @JoinColumn(name = "condition_id")
    private RuleConditionJpaEntity condition;

    @Column(nullable = false)
    private BigDecimal thresholdValue;

    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_actuator_id", nullable = false)
    private ActuatorJpaEntity targetActuator;

    private Boolean isActive;

    public AutomationRuleJpaEntity() {}

    public AutomationRuleJpaEntity(UUID automationId, String name, SensorJpaEntity inputSensor, RuleConditionJpaEntity condition, BigDecimal thresholdValue, ActionType actionType, ActuatorJpaEntity targetActuator, Boolean isActive) {
        this.automationId = automationId;
        this.name = name;
        this.inputSensor = inputSensor;
        this.condition = condition;
        this.thresholdValue = thresholdValue;
        this.actionType = actionType;
        this.targetActuator = targetActuator;
        this.isActive = isActive;
    }
}
