package henrique.igor.iiot.infrastructure.persistence.entities;

import henrique.igor.iiot.domain.entities.enums.AlertStatus;
import henrique.igor.iiot.domain.entities.enums.Severity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.*;

@Entity
@Table(name = "alerts")
@Getter
@Setter
public class AlertJpaEntity {

    @Id
    private UUID alertId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipment_id", nullable = false)
    private EquipmentJpaEntity equipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_group_id")
    private RuleGroupJpaEntity ruleGroup;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;

    @Column(nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acknowledged_by")
    private UserJpaEntity acknowledgedBy;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime triggeredAt;

    private OffsetDateTime resolvedAt;

    @OneToMany(mappedBy = "alert", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AlertSensorSnapshotJpaEntity> sensorSnapshots = new ArrayList<>();

    public AlertJpaEntity(){}

    public AlertJpaEntity(UUID alertId, EquipmentJpaEntity equipment, RuleGroupJpaEntity ruleGroup, Severity severity, String message, AlertStatus status, UserJpaEntity acknowledgedBy, OffsetDateTime triggeredAt, OffsetDateTime resolvedAt, List<AlertSensorSnapshotJpaEntity> sensorSnapshots) {
        this.alertId = alertId;
        this.equipment = equipment;
        this.ruleGroup = ruleGroup;
        this.severity = severity;
        this.message = message;
        this.status = status;
        this.acknowledgedBy = acknowledgedBy;
        this.triggeredAt = triggeredAt;
        this.resolvedAt = resolvedAt;
        this.sensorSnapshots = sensorSnapshots;
    }
}
