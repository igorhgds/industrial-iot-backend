package henrique.igor.iiot.infrastructure.persistence.mappers;

import henrique.igor.iiot.domain.entities.*;
import henrique.igor.iiot.infrastructure.persistence.entities.SensorJpaEntity;
import henrique.igor.iiot.infrastructure.persistence.entities.TelemetryDataJpaEntity;

import java.time.OffsetDateTime;
import java.util.UUID;

public class TelemetryDataMapper {

    public static TelemetryDataJpaEntity toJpaEntity(TelemetryData domain) {
        if (domain == null) return null;

        SensorJpaEntity sensorJpa = domain.getSensor() != null
                ? SensorMapper.toJpaEntity(domain.getSensor())
                : null;

        UUID sensorId = domain.getSensor() != null ? domain.getSensor().getSensorId() : null;
        TelemetryDataId id = new TelemetryDataId(sensorId, domain.getTimestamp());

        return new TelemetryDataJpaEntity(
                id,
                sensorJpa,
                domain.getValue(),
                domain.getRawPayload()
        );
    }

    public static TelemetryData toDomain(TelemetryDataJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;

        Sensor sensorDomain = jpaEntity.getSensor() != null
                ? SensorMapper.toDomain(jpaEntity.getSensor())
                : null;

        OffsetDateTime timestamp = jpaEntity.getId() != null
                ? jpaEntity.getId().getTimestamp()
                : null;

        return new TelemetryData(
                sensorDomain,
                timestamp,
                jpaEntity.getValue(),
                jpaEntity.getRawPayload()
        );
    }
}