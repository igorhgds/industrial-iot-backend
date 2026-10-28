package henrique.igor.iiot.application.usecases.telemetry.dto;

import java.math.BigDecimal;

public record TelemetryReadingInput(
        String sensorCode,
        BigDecimal value
) {
}
