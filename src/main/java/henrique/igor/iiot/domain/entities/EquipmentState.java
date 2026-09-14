package henrique.igor.iiot.domain.entities;

import henrique.igor.iiot.domain.entities.enums.HealthStatus;

import java.time.OffsetDateTime;

public class EquipmentState {

    private Equipment equipmentId;
    private HealthStatus status;
    private String lastPayload;
    private OffsetDateTime updatedAt;

    public EquipmentState(Equipment equipmentId, HealthStatus status, String lastPayload, OffsetDateTime updatedAt) {
        this.equipmentId = equipmentId;
        this.status = status;
        this.lastPayload = lastPayload;
        this.updatedAt = updatedAt;
    }

    public Equipment getEquipmentId() {return equipmentId;}
    public HealthStatus getStatus() {return status;}
    public String getLastPayload() {return lastPayload;}
    public OffsetDateTime getUpdatedAt() {return updatedAt;}
}
