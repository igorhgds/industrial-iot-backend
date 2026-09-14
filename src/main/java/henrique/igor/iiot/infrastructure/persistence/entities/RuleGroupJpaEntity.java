package henrique.igor.iiot.infrastructure.persistence.entities;

import henrique.igor.iiot.domain.entities.enums.LogicalOperator;
import henrique.igor.iiot.domain.entities.enums.Severity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.*;

@Entity
@Table(name = "rule_groups")
@Getter
@Setter
public class RuleGroupJpaEntity {

    @Id
    private UUID ruleGroupId;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private EquipmentJpaEntity equipment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LogicalOperator operatorType;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @OneToMany(mappedBy = "ruleGroup", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RuleConditionJpaEntity> conditions = new ArrayList<>();

    public RuleGroupJpaEntity(){}

    public RuleGroupJpaEntity(UUID ruleGroupId, String name, String description, EquipmentJpaEntity equipment, Severity severity, LogicalOperator operatorType, Boolean isActive, OffsetDateTime createdAt) {
        this.ruleGroupId = ruleGroupId;
        this.name = name;
        this.description = description;
        this.equipment = equipment;
        this.severity = severity;
        this.operatorType = operatorType;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }
}