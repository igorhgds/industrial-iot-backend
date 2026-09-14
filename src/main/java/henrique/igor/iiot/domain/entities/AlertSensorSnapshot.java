package henrique.igor.iiot.domain.entities;

import java.math.BigDecimal;
import java.util.UUID;

public class AlertSensorSnapshot {

    private final UUID snapshotId;
    private final Sensor sensor;
    private final BigDecimal valueAtTrigger;

    public AlertSensorSnapshot(Sensor sensor, BigDecimal valueAtTrigger) {
        this(UUID.randomUUID(), sensor, valueAtTrigger);
    }

    public AlertSensorSnapshot(UUID snapshotId, Sensor sensor, BigDecimal valueAtTrigger) {
        this.snapshotId = snapshotId;
        this.sensor = sensor;
        this.valueAtTrigger = valueAtTrigger;
    }

    public UUID getSnapshotId() {return snapshotId;}
    public Sensor getSensor() {return sensor;}
    public BigDecimal getValueAtTrigger() {return valueAtTrigger;}
}
