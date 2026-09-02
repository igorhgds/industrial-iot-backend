package henrique.igor.iiot.domain.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TelemetryData {

    private UUID telemetryId;
    private LocalDateTime timestamp;
    private BigDecimal value;
    private String rawPayload;
    private Sensor sensor;

    public TelemetryData(BigDecimal value, String rawPayload, Sensor sensor) {
        this.telemetryId = UUID.randomUUID();
        this.timestamp = LocalDateTime.now();
        this.value = value;
        this.rawPayload = rawPayload;
        this.sensor = sensor;
    }

    public TelemetryData(UUID telemetryId, LocalDateTime timestamp, BigDecimal value, String rawPayload, Sensor sensor) {
        this.telemetryId = telemetryId;
        this.timestamp = timestamp;
        this.value = value;
        this.rawPayload = rawPayload;
        this.sensor = sensor;
    }

    public UUID getTelemetryId() {return telemetryId;}
    public LocalDateTime getTimestamp() {return timestamp;}
    public BigDecimal getValue() {return value;}
    public String getRawPayload() {return rawPayload;}
    public Sensor getSensor() {return sensor;}
}
