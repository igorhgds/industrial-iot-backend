package henrique.igor.iiot.infrastructure.persistence.entities;

import henrique.igor.iiot.domain.entities.enums.WorkOrderPriority;
import henrique.igor.iiot.domain.entities.enums.WorkOrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "work_orders")
@Getter
@Setter
public class WorkOrderJpaEntity {

    @Id
    private UUID workOrderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alert_id")
    private AlertJpaEntity alert;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id")
    private EquipmentJpaEntity equipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private UserJpaEntity assignedTo;

    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private WorkOrderPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkOrderStatus status;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    private OffsetDateTime closedAt;

    public WorkOrderJpaEntity() {}

    public WorkOrderJpaEntity(UUID workOrderId, AlertJpaEntity alert, EquipmentJpaEntity equipment, UserJpaEntity assignedTo, String title, String description, WorkOrderPriority priority, WorkOrderStatus status, OffsetDateTime createdAt, OffsetDateTime closedAt) {
        this.workOrderId = workOrderId;
        this.alert = alert;
        this.equipment = equipment;
        this.assignedTo = assignedTo;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.createdAt = createdAt;
        this.closedAt = closedAt;
    }
}
