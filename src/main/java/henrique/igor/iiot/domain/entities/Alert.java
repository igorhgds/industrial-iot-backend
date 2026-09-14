package henrique.igor.iiot.domain.entities;

import henrique.igor.iiot.domain.entities.enums.AlertStatus;
import henrique.igor.iiot.domain.entities.enums.Severity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class Alert {

    private final UUID alertId;
    private final UUID equipment;
    private final UUID sensor;
    private Severity severity;
    private String message;
    private BigDecimal valueAtTrigger;
    private AlertStatus status;
    private UUID acknowledgedBy;
    private OffsetDateTime triggeredAt;
    private OffsetDateTime resolvedAt;
}
