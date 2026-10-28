package henrique.igor.iiot.domain.entities;

import henrique.igor.iiot.domain.entities.enums.UserRole;
import henrique.igor.iiot.domain.entities.enums.WorkOrderPriority;
import henrique.igor.iiot.domain.entities.enums.WorkOrderStatus;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;

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

    public void assignToTechnician(User technician) {
        if (technician == null) {
            throw new DomainValidationException("Technician user cannot be null.");
        }
        if (this.status == WorkOrderStatus.COMPLETED || this.status == WorkOrderStatus.CANCELLED) {
            throw new DomainValidationException("Cannot assign a technician to a closed WorkOrder.");
        }
        if (technician.getUserRole() != UserRole.TECHNICIAN) {
            throw new DomainValidationException("Assigned user must have the TECHNICIAN role.");
        }
        this.assignedTo = technician;
    }

    public void transitionStatus(WorkOrderStatus newStatus) {
        if (newStatus == null) {
            throw new DomainValidationException("WorkOrder status cannot be null.");
        }
        if (!isValidTransition(this.status, newStatus)) {
            throw new DomainValidationException(
                    "Invalid WorkOrder status transition from " + this.status + " to " + newStatus + "."
            );
        }
        this.status = newStatus;
        if (newStatus == WorkOrderStatus.COMPLETED || newStatus == WorkOrderStatus.CANCELLED) {
            this.closedAt = OffsetDateTime.now();
        }
    }

    public void completeWithResolutionNotes(String resolutionNotes) {
        if (this.status == WorkOrderStatus.COMPLETED || this.status == WorkOrderStatus.CANCELLED) {
            throw new DomainValidationException("WorkOrder is already closed.");
        }
        if (resolutionNotes == null || resolutionNotes.isBlank()) {
            throw new DomainValidationException("Resolution notes cannot be null or blank when closing a WorkOrder.");
        }

        String trimmedNotes = resolutionNotes.trim();
        this.description = (this.description != null && !this.description.isBlank())
                ? this.description + "\nResolution: " + trimmedNotes
                : "Resolution: " + trimmedNotes;

        this.status = WorkOrderStatus.COMPLETED;
        this.closedAt = OffsetDateTime.now();
    }

    private boolean isValidTransition(WorkOrderStatus current, WorkOrderStatus target) {
        if (current == null || current == target) {
            return false;
        }
        return switch (current) {
            case OPEN -> target == WorkOrderStatus.IN_PROGRESS || target == WorkOrderStatus.CANCELLED;
            case IN_PROGRESS -> target == WorkOrderStatus.COMPLETED || target == WorkOrderStatus.CANCELLED;
            case COMPLETED, CANCELLED -> false;
        };
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
