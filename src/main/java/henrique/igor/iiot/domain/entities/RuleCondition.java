package henrique.igor.iiot.domain.entities;

import henrique.igor.iiot.domain.entities.enums.ComparisonOperator;

import java.math.BigDecimal;
import java.util.UUID;

public class RuleCondition {

    private final UUID conditionId;
    private RuleGroup ruleGroup;
    private Sensor sensor;
    private ComparisonOperator operator;
    private BigDecimal thresholdValue;

    public RuleCondition(RuleGroup ruleGroup, Sensor sensor, ComparisonOperator operator, BigDecimal thresholdValue){
        this(UUID.randomUUID(), ruleGroup, sensor, operator, thresholdValue);
    }

    public RuleCondition(UUID conditionId, RuleGroup ruleGroup, Sensor sensor, ComparisonOperator operator, BigDecimal thresholdValue) {
        this.conditionId = conditionId;
        this.ruleGroup = ruleGroup;
        this.sensor = sensor;
        this.operator = operator;
        this.thresholdValue = thresholdValue;
    }

    public boolean evaluate(BigDecimal value) {
        if (value == null || thresholdValue == null || operator == null) {
            return false;
        }
        int comparison = value.compareTo(thresholdValue);
        return switch (operator) {
            case GREATER_THAN -> comparison > 0;
            case LESS_THAN -> comparison < 0;
            case EQUALS -> comparison == 0;
            case GREATER_THAN_OR_EQUAL -> comparison >= 0;
            case LESS_THAN_OR_EQUAL -> comparison <= 0;
        };
    }

    public UUID getConditionId() {return conditionId;}
    public RuleGroup getRuleGroup() {return ruleGroup;}
    public Sensor getSensor() {return sensor;}
    public ComparisonOperator getOperator() {return operator;}
    public BigDecimal getThresholdValue() {return thresholdValue;}
}
