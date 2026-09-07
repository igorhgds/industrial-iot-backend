package henrique.igor.iiot.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;

@Entity
@Table(name = "telemetry_data")
@Getter
@Setter
public class TelemetryDataJpaEntity {

    @EmbeddedId
    private TelemetryDataId id;

    @MapsId("sensorId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sensor_id", nullable = false)
    private SensorJpaEntity sensor;

    @Column(name = "value")
    private BigDecimal value;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "raw_payload", columnDefinition = "jsonb")
    private String rawPayload;

    public TelemetryDataJpaEntity() {}

    public TelemetryDataJpaEntity(TelemetryDataId id, SensorJpaEntity sensor, BigDecimal value, String rawPayload) {
        this.id = id;
        this.sensor = sensor;
        this.value = value;
        this.rawPayload = rawPayload;
    }
}
