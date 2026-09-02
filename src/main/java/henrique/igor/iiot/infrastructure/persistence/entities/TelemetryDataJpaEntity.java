package henrique.igor.iiot.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "telemetry_data")
@Getter
@Setter
public class TelemetryDataJpaEntity {

    @Id
    private UUID telemetryId;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private BigDecimal value;

    private String rawPayload;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id")
    private SensorJpaEntity sensor;

    public TelemetryDataJpaEntity(){}

    public TelemetryDataJpaEntity(UUID telemetryId, LocalDateTime timestamp, BigDecimal value, String rawPayload, SensorJpaEntity sensor) {
        this.telemetryId = telemetryId;
        this.timestamp = timestamp;
        this.value = value;
        this.rawPayload = rawPayload;
        this.sensor = sensor;
    }
}
