package henrique.igor.iiot.infrastructure.messaging.dto;

import java.math.BigDecimal;

public record ReadingDTO(
        String sensorCode,
        BigDecimal value
) {
}
