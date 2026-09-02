package henrique.igor.iiot.domain.entities;

import henrique.igor.iiot.domain.entities.enums.EquipStatus;
import henrique.igor.iiot.domain.entities.enums.EquipType;

import java.time.LocalDateTime;
import java.util.UUID;

public class Equipment {

    private UUID equipmentId;
    private String equipCode;
    private EquipType type;
    private EquipStatus status;
    private Sector sector;
    private LocalDateTime createdAt;

    public Equipment(String equipCode, EquipType type, EquipStatus status, Sector sector) {
        this.equipmentId = UUID.randomUUID();
        this.equipCode = equipCode;
        this.type = type;
        this.status = status;
        this.sector = sector;
        this.createdAt = LocalDateTime.now();
    }

    public Equipment(UUID equipmentId, String equipCode, EquipType type, EquipStatus status, Sector sector, LocalDateTime createdAt) {
        this.equipmentId = equipmentId;
        this.equipCode = equipCode;
        this.type = type;
        this.status = status;
        this.sector = sector;
        this.createdAt = createdAt;
    }

    public UUID getEquipmentId() {return equipmentId;}
    public String getEquipCode() {return equipCode;}
    public EquipType getType() {return type;}
    public EquipStatus getStatus() {return status;}
    public Sector getSector() {return sector;}
    public LocalDateTime getCreatedAt() {return createdAt;}
}
