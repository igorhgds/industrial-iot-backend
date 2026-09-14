package henrique.igor.iiot.domain.entities;

import henrique.igor.iiot.domain.entities.enums.ActionType;

import java.math.BigDecimal;
import java.util.UUID;

public class AutomationRule {
    private final UUID automationId;
    private String name;
    private Sensor inputSensorId;
    private RuleCondition condition;
    private BigDecimal thresholdValue;
    private ActionType actionType;
    private Actuator targetActuator;
    private Boolean isActive;

    public AutomationRule(String name, Sensor inputSensorId, RuleCondition condition, BigDecimal thresholdValue, ActionType actionType, Actuator targetActuator, Boolean isActive){
        this(UUID.randomUUID(), name, inputSensorId, condition, thresholdValue, actionType, targetActuator, isActive);
    }

    public AutomationRule(UUID automationId, String name, Sensor inputSensorId, RuleCondition condition, BigDecimal thresholdValue, ActionType actionType, Actuator targetActuator, Boolean isActive) {
        this.automationId = automationId;
        this.name = name;
        this.inputSensorId = inputSensorId;
        this.condition = condition;
        this.thresholdValue = thresholdValue;
        this.actionType = actionType;
        this.targetActuator = targetActuator;
        this.isActive = isActive;
    }

    public UUID getAutomationId() {return automationId;}
    public String getName() {return name;}
    public Sensor getInputSensorId() {return inputSensorId;}
    public RuleCondition getCondition() {return condition;}
    public BigDecimal getThresholdValue() {return thresholdValue;}
    public ActionType getActionType() {return actionType;}
    public Actuator getTargetActuator() {return targetActuator;}
    public Boolean getActive() {return isActive;}
}
