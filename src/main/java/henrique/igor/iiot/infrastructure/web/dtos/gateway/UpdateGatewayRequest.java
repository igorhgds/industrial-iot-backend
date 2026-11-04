package henrique.igor.iiot.infrastructure.web.dtos.gateway;

import henrique.igor.iiot.application.usecases.gateway.dto.UpdateGatewayInput;
import henrique.igor.iiot.domain.entities.enums.GatewayStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Payload for updating an existing IoT edge gateway")
public record UpdateGatewayRequest(
        @Schema(description = "Updated IPv4 or IPv6 address of the gateway", example = "192.168.10.55")
        @Size(max = 45, message = "IP address must not exceed 45 characters")
        String ipAddress,

        @Schema(description = "Updated firmware version", example = "v1.5.0")
        @Size(max = 20, message = "Firmware version must not exceed 20 characters")
        String firmwareVersion,

        @Schema(description = "Operational status of the gateway", example = "ONLINE")
        GatewayStatus status,

        @Schema(description = "New sector ID to relocate the gateway", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID sectorId,

        @Schema(description = "Timestamp of the last gateway heartbeat", example = "2026-10-06T12:30:00Z")
        OffsetDateTime lastPing
) {
    public UpdateGatewayInput toInput(UUID gatewayId) {
        return new UpdateGatewayInput(gatewayId, ipAddress, firmwareVersion, status, sectorId, lastPing);
    }
}
