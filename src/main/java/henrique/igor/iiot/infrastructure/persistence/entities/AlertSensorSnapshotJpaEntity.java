package henrique.igor.iiot.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "alert_sensor_snapshots")
@Getter
@Setter
public class AlertSensorSnapshotJpaEntity {

    @Id
    private UUID snapshotId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    private SensorJpaEntity sensor;

    private BigDecimal valueAtTrigger;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alert_id", nullable = false)
    private AlertJpaEntity alert;

    public AlertSensorSnapshotJpaEntity(){}

    public AlertSensorSnapshotJpaEntity(UUID snapshotId, AlertJpaEntity alert, SensorJpaEntity sensor, BigDecimal valueAtTrigger) {
        this.snapshotId = snapshotId;
        this.alert = alert;
        this.sensor = sensor;
        this.valueAtTrigger = valueAtTrigger;
    }

    public AlertSensorSnapshotJpaEntity(UUID snapshotId, SensorJpaEntity sensor, BigDecimal valueAtTrigger) {
        this(snapshotId, null, sensor, valueAtTrigger);
    }
}
