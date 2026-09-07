package henrique.igor.iiot.domain.entities;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class TelemetryData {

    private final Sensor sensor;
    private final OffsetDateTime timestamp;
    private final BigDecimal value;
    private final String rawPayload;

    // Factory / Constructor para novas leituras da aplicação
    public TelemetryData(Sensor sensor, BigDecimal value, String rawPayload) {
        this(sensor, OffsetDateTime.now(), value, rawPayload);
    }

    // Constructor completo para reconstituição do banco
    public TelemetryData(Sensor sensor, OffsetDateTime timestamp, BigDecimal value, String rawPayload) {
        this.sensor = sensor;
        this.timestamp = timestamp;
        this.value = value;
        this.rawPayload = rawPayload;
    }

    public Sensor getSensor() { return sensor; }
    public OffsetDateTime getTimestamp() { return timestamp; }
    public BigDecimal getValue() { return value; }
    public String getRawPayload() { return rawPayload; }
}
