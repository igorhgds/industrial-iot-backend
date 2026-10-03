package henrique.igor.iiot.application.usecases.gateway.dto;

import henrique.igor.iiot.domain.entities.enums.GatewayStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UpdateGatewayInput(
        UUID gatewayId,
        String ipAddress,
        String firmwareVersion,
        GatewayStatus status,
        UUID sectorId,
        OffsetDateTime lastPing
) {
}
