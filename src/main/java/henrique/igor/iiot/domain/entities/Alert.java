package henrique.igor.iiot.domain.entities;

import henrique.igor.iiot.domain.entities.enums.AlertStatus;
import henrique.igor.iiot.domain.entities.enums.Severity;

import java.time.OffsetDateTime;
import java.util.*;

public class Alert {

    private final UUID alertId;
    private final Equipment equipment;
    private final RuleGroup ruleGroup;
    private Severity severity;
    private String message;
    private AlertStatus status;
    private User acknowledgedBy;
    private OffsetDateTime triggeredAt;
    private OffsetDateTime resolvedAt;
    private final List<AlertSensorSnapshot> sensorSnapshots;

    public Alert(Equipment equipment, RuleGroup ruleGroup, Severity severity, String message, List<AlertSensorSnapshot> sensorSnapshots) {
        this(UUID.randomUUID(), equipment, ruleGroup, severity, message, AlertStatus.PENDING, null, OffsetDateTime.now(), null, sensorSnapshots);
    }

    public Alert(UUID alertId, Equipment equipment, RuleGroup ruleGroup, Severity severity, String message, AlertStatus status, User acknowledgedBy, OffsetDateTime triggeredAt, OffsetDateTime resolvedAt, List<AlertSensorSnapshot> sensorSnapshots) {
        this.alertId = alertId;
        this.equipment = equipment;
        this.ruleGroup = ruleGroup;
        this.severity = severity;
        this.message = message;
        this.status = status;
        this.acknowledgedBy = acknowledgedBy;
        this.triggeredAt = triggeredAt;
        this.resolvedAt = resolvedAt;
        this.sensorSnapshots = sensorSnapshots != null ? new ArrayList<>(sensorSnapshots) : new ArrayList<>();
    }

    public UUID getAlertId() {return alertId;}
    public Equipment getEquipment() {return equipment;}
    public RuleGroup getRuleGroup() {return ruleGroup;}
    public Severity getSeverity() {return severity;}
    public String getMessage() {return message;}
    public AlertStatus getStatus() {return status;}
    public User getAcknowledgedBy() {return acknowledgedBy;}
    public OffsetDateTime getTriggeredAt() {return triggeredAt;}
    public OffsetDateTime getResolvedAt() {return resolvedAt;}
    public List<AlertSensorSnapshot> getSensorSnapshots() {return Collections.unmodifiableList(sensorSnapshots);}
}

