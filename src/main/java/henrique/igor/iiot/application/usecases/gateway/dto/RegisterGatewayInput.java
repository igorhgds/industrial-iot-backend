package henrique.igor.iiot.application.usecases.gateway.dto;

import java.util.UUID;

public record RegisterGatewayInput(
        String code,
        String macAddress,
        String ipAddress,
        String firmwareVersion,
        UUID sectorId
) {
}
