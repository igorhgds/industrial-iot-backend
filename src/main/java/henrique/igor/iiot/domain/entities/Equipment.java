package henrique.igor.iiot.domain.entities;

import henrique.igor.iiot.domain.entities.enums.EquipStatus;
import henrique.igor.iiot.domain.entities.enums.EquipType;

import java.time.LocalDateTime;
import java.util.UUID;

public class Equipment {

    private final UUID equipmentId;
    private final String equipCode;
    private EquipType type;
    private EquipStatus status;
    private Sector sector;
    private Gateway gateway;
    private final LocalDateTime createdAt;

    public Equipment(String equipCode, EquipType type, EquipStatus status, Sector sector, Gateway gateway) {
        this(UUID.randomUUID(), equipCode, type, status, sector, gateway ,LocalDateTime.now());
    }

    public Equipment(UUID equipmentId, String equipCode, EquipType type, EquipStatus status, Sector sector, Gateway gateway ,LocalDateTime createdAt) {
        this.equipmentId = equipmentId;
        this.equipCode = equipCode;
        this.type = type;
        this.status = status;
        this.sector = sector;
        this.gateway = gateway;
        this.createdAt = createdAt;
    }

    public UUID getEquipmentId() {return equipmentId;}
    public String getEquipCode() {return equipCode;}
    public EquipType getType() {return type;}
    public EquipStatus getStatus() {return status;}
    public Sector getSector() {return sector;}
    public Gateway getGateway() {return gateway;}
    public LocalDateTime getCreatedAt() {return createdAt;}
}
