package henrique.igor.iiot.domain.entities;

import henrique.igor.iiot.domain.entities.enums.WorkOrderPriority;
import henrique.igor.iiot.domain.entities.enums.WorkOrderStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public class WorkOrder {

    private final UUID workOrderId;
    private Alert alert;
    private Equipment equipment;
    private User assignedTo;
    private String title;
    private String description;
    private WorkOrderPriority priority;
    private WorkOrderStatus status;
    private final OffsetDateTime createdAt;
    private OffsetDateTime closedAt;

    public WorkOrder(String title, String description, WorkOrderPriority priority, Alert alert, Equipment equipment, User assignedTo) {
        this(UUID.randomUUID(), alert, equipment, assignedTo, title, description, priority, WorkOrderStatus.OPEN, OffsetDateTime.now(), null);
    }

    public WorkOrder(UUID workOrderId, Alert alert, Equipment equipment, User assignedTo, String title, String description, WorkOrderPriority priority, WorkOrderStatus status, OffsetDateTime createdAt, OffsetDateTime closedAt) {
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

    public UUID getWorkOrderId() { return workOrderId; }
    public Alert getAlert() { return alert; }
    public Equipment getEquipment() { return equipment; }
    public User getAssignedTo() { return assignedTo; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public WorkOrderPriority getPriority() { return priority; }
    public WorkOrderStatus getStatus() { return status; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getClosedAt() { return closedAt; }
}
