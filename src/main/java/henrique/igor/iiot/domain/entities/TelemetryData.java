package henrique.igor.iiot.domain.entities;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class TelemetryData {

    private final Sensor sensor;
    private final OffsetDateTime timestamp;
    private final BigDecimal value;

    public TelemetryData(Sensor sensor, BigDecimal value) {
        this(sensor, OffsetDateTime.now(), value);
    }

    public TelemetryData(Sensor sensor, OffsetDateTime timestamp, BigDecimal value) {
        this.sensor = sensor;
        this.timestamp = timestamp;
        this.value = value;
    }

    public Sensor getSensor() { return sensor; }
    public OffsetDateTime getTimestamp() { return timestamp; }
    public BigDecimal getValue() { return value; }
}
