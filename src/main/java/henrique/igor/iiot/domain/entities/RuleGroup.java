package henrique.igor.iiot.domain.entities;

import henrique.igor.iiot.domain.entities.enums.LogicalOperator;
import henrique.igor.iiot.domain.entities.enums.Severity;

import java.time.OffsetDateTime;
import java.util.UUID;

public class RuleGroup {

    private final UUID ruleGroupId;
    private String name;
    private String description;
    private Equipment equipment;
    private Severity severity;
    private LogicalOperator operatorType;
    private Boolean isActive;
    private final OffsetDateTime createdAt;

    public RuleGroup(String name, String description, Equipment equipment, LogicalOperator operatorType){
        this(UUID.randomUUID(), name, description, equipment, Severity.REGULAR, operatorType, false, OffsetDateTime.now());
    }

    public RuleGroup(UUID ruleGroupId, String name, String description, Equipment equipment, Severity severity, LogicalOperator operatorType, Boolean isActive, OffsetDateTime createdAt) {
        this.ruleGroupId = ruleGroupId;
        this.name = name;
        this.description = description;
        this.equipment = equipment;
        this.severity = severity;
        this.operatorType = operatorType;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void updateInfo(String name, String description, Severity severity, LogicalOperator operatorType) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
        if (severity != null) {
            this.severity = severity;
        }
        if (operatorType != null) {
            this.operatorType = operatorType;
        }
    }

    public UUID getRuleGroupId() {return ruleGroupId;}
    public String getName() {return name;}
    public String getDescription() {return description;}
    public Equipment getEquipment() {return equipment;}
    public Severity getSeverity() {return severity;}
    public LogicalOperator getOperatorType() {return operatorType;}
    public Boolean getActive() {return isActive;}
    public OffsetDateTime getCreatedAt() {return createdAt;}
}
