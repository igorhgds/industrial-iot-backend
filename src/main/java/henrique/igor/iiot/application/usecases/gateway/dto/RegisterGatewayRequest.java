package henrique.igor.iiot.application.usecases.gateway.dto;

import henrique.igor.iiot.domain.entities.enums.GatewayStatus;

import java.util.UUID;

public record RegisterGatewayRequest(
        String code,
        String macAddress,
        String ipAddress,
        String firmwareVersion,
        GatewayStatus status,
        UUID sectorId
) {
}
