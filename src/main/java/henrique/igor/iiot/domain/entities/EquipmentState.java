package henrique.igor.iiot.domain.entities;

import henrique.igor.iiot.domain.entities.enums.HealthStatus;

import java.time.LocalDateTime;

public class EquipmentState {

    private Equipment equipmentId;
    private HealthStatus status;
    private String lastPayload;
    private LocalDateTime updatedAt;

    public EquipmentState(Equipment equipmentId, HealthStatus status, String lastPayload, LocalDateTime updatedAt) {
        this.equipmentId = equipmentId;
        this.status = status;
        this.lastPayload = lastPayload;
        this.updatedAt = updatedAt;
    }

    public Equipment getEquipmentId() {return equipmentId;}
    public HealthStatus getStatus() {return status;}
    public String getLastPayload() {return lastPayload;}
    public LocalDateTime getUpdatedAt() {return updatedAt;}
}
