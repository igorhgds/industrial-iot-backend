package henrique.igor.iiot.infrastructure.web.dtos.gateway;

import henrique.igor.iiot.application.usecases.gateway.dto.RegisterGatewayInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Payload for registering a new IoT edge gateway")
public record RegisterGatewayRequest(
        @Schema(
                description = "Optional custom gateway code. If omitted or blank, a code is generated automatically",
                example = "GW-USI-001"
        )
        @Size(max = 50, message = "Gateway code must not exceed 50 characters")
        String code,

        @Schema(description = "Hardware MAC address of the gateway", example = "00:1B:44:11:3A:B7")
        @NotBlank(message = "MAC address is required")
        @Size(max = 17, message = "MAC address must not exceed 17 characters")
        String macAddress,

        @Schema(description = "IPv4 or IPv6 address of the gateway", example = "192.168.10.50")
        @Size(max = 45, message = "IP address must not exceed 45 characters")
        String ipAddress,

        @Schema(description = "Installed firmware version", example = "v1.4.2")
        @Size(max = 20, message = "Firmware version must not exceed 20 characters")
        String firmwareVersion,

        @Schema(description = "Optional sector ID where the gateway is installed", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID sectorId
) {
    public RegisterGatewayInput toInput() {
        return new RegisterGatewayInput(code, macAddress, ipAddress, firmwareVersion, sectorId);
    }
}
