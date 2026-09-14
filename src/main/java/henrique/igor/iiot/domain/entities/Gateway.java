package henrique.igor.iiot.domain.entities;

import henrique.igor.iiot.domain.entities.enums.GatewayStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Gateway {

    private final UUID gatewayId;
    private final String code;
    private final String macAddress;
    private String ipAddress;
    private String firmwareVersion;
    private GatewayStatus status;
    private Sector sector;
    private OffsetDateTime lastPing;
    private final OffsetDateTime createdAt;

    public Gateway(String code, String macAddress, String ipAddress, String firmwareVersion, GatewayStatus status, Sector sector, OffsetDateTime lastPing){
        this(UUID.randomUUID(), code, macAddress, ipAddress, firmwareVersion, status, sector, lastPing, OffsetDateTime.now());
    }

    public Gateway(UUID gatewayId, String code, String macAddress, String ipAddress, String firmwareVersion, GatewayStatus status, Sector sector, OffsetDateTime lastPing, OffsetDateTime createdAt) {
        this.gatewayId = gatewayId;
        this.code = code;
        this.macAddress = macAddress;
        this.ipAddress = ipAddress;
        this.firmwareVersion = firmwareVersion;
        this.status = status;
        this.sector = sector;
        this.lastPing = lastPing;
        this.createdAt = createdAt;
    }

    public UUID getGatewayId(){return gatewayId;}
    public String getCode() {return code;}
    public String getMacAddress() {return macAddress;}
    public String getIpAddress() {return ipAddress;}
    public String getFirmwareVersion() {return firmwareVersion;}
    public GatewayStatus getStatus() {return status;}
    public Sector getSector() {return sector;}
    public OffsetDateTime getLastPing() {return lastPing;}
    public OffsetDateTime getCreatedAt() {return createdAt;}
}
