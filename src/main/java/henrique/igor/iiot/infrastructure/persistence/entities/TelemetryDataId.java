package henrique.igor.iiot.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class TelemetryDataId implements Serializable {

    @Column(name = "sensor_id")
    private UUID sensorId;

    @Column(name = "timestamp")
    private OffsetDateTime timestamp;

    public TelemetryDataId() {}

    public TelemetryDataId(UUID sensorId, OffsetDateTime timestamp) {
        this.sensorId = sensorId;
        this.timestamp = timestamp;
    }

    public UUID getSensorId() { return sensorId; }
    public void setSensorId(UUID sensorId) { this.sensorId = sensorId; }

    public OffsetDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(OffsetDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TelemetryDataId that = (TelemetryDataId) o;
        return Objects.equals(sensorId, that.sensorId) && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sensorId, timestamp);
    }
}