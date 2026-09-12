package henrique.igor.iiot.infrastructure.persistence.entities;

import henrique.igor.iiot.domain.entities.enums.ComparisonOperator;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "rule_conditions")
@Getter
@Setter
public class RuleConditionJpaEntity {

    @Id
    private UUID conditionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_group_id", nullable = false)
    private RuleGroupJpaEntity ruleGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    private SensorJpaEntity sensor;

    @Enumerated(EnumType.STRING)
    private ComparisonOperator operator;

    private BigDecimal thresholdValue;

    public RuleConditionJpaEntity(){}

    public RuleConditionJpaEntity(UUID conditionId, RuleGroupJpaEntity ruleGroup, SensorJpaEntity sensor, ComparisonOperator operator, BigDecimal thresholdValue) {
        this.conditionId = conditionId;
        this.ruleGroup = ruleGroup;
        this.sensor = sensor;
        this.operator = operator;
        this.thresholdValue = thresholdValue;
    }
}
