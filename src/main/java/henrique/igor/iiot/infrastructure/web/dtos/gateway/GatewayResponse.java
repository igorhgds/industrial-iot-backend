package henrique.igor.iiot.infrastructure.web.dtos.gateway;

import henrique.igor.iiot.domain.entities.Gateway;
import henrique.igor.iiot.domain.entities.enums.GatewayStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "IoT edge gateway representation returned by the API")
public record GatewayResponse(
        @Schema(description = "Unique identifier of the gateway", example = "660e8400-e29b-41d4-a716-446655440111")
        UUID gatewayId,

        @Schema(description = "Unique business code of the gateway", example = "GW-USI-001")
        String gatewayCode,

        @Schema(description = "Hardware MAC address", example = "00:1B:44:11:3A:B7")
        String macAddress,

        @Schema(description = "IPv4 or IPv6 address", example = "192.168.10.50")
        String ipAddress,

        @Schema(description = "Installed firmware version", example = "v1.4.2")
        String firmwareVersion,

        @Schema(description = "Current operational status", example = "ONLINE")
        GatewayStatus status,

        @Schema(description = "Identifier of the sector where the gateway is installed", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID sectorId,

        @Schema(description = "Name of the sector where the gateway is installed", example = "Usinagem")
        String sectorName,

        @Schema(description = "Timestamp of the last communication ping", example = "2026-10-06T12:30:00Z")
        OffsetDateTime lastPing,

        @Schema(description = "Timestamp when the gateway was registered", example = "2026-10-06T12:00:00Z")
        OffsetDateTime createdAt
) {
    public static GatewayResponse fromDomain(Gateway gateway) {
        return new GatewayResponse(
                gateway.getGatewayId(),
                gateway.getGatewayCode(),
                gateway.getMacAddress(),
                gateway.getIpAddress(),
                gateway.getFirmwareVersion(),
                gateway.getStatus(),
                gateway.getSector() != null ? gateway.getSector().getSectorId() : null,
                gateway.getSector() != null ? gateway.getSector().getName() : null,
                gateway.getLastPing(),
                gateway.getCreatedAt()
        );
    }
}
