package henrique.igor.iiot.domain.entities;

import henrique.igor.iiot.domain.entities.enums.GatewayStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class Gateway {

    private final UUID gatewayId;
    private final String code;
    private final String macAddress;
    private String ipAddress;
    private String firmwareVersion;
    private GatewayStatus status;
    private Sector sector;
    private LocalDateTime lastPing;
    private final LocalDateTime createdAt;

    public Gateway(String code, String macAddress, String ipAddress, String firmwareVersion, GatewayStatus status, Sector sector, LocalDateTime lastPing){
        this(UUID.randomUUID(), code, macAddress, ipAddress, firmwareVersion, status, sector, lastPing, LocalDateTime.now());
    }

    public Gateway(UUID gatewayId, String code, String macAddress, String ipAddress, String firmwareVersion, GatewayStatus status, Sector sector, LocalDateTime lastPing, LocalDateTime createdAt) {
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
    public LocalDateTime getLastPing() {return lastPing;}
    public LocalDateTime getCreatedAt() {return createdAt;}
}
