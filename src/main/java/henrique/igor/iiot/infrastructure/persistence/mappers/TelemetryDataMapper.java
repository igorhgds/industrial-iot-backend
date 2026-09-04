package henrique.igor.iiot.infrastructure.persistence.mappers;

import henrique.igor.iiot.domain.entities.TelemetryData;
import henrique.igor.iiot.infrastructure.persistence.entities.TelemetryDataJpaEntity;

public class TelemetryDataMapper {

    public static TelemetryDataJpaEntity toJpaEntity(TelemetryData domain){
        if (domain == null) return null;
        return new TelemetryDataJpaEntity(
                domain.getTelemetryId(),
                domain.getTimestamp(),
                domain.getValue(),
                domain.getRawPayload(),
                domain.getSensor() != null ? SensorMapper.toJpaEntity(domain.getSensor()) : null
        );
    }

    public static TelemetryData toDomain(TelemetryDataJpaEntity jpaEntity){
        if (jpaEntity == null) return null;
        return new TelemetryData(
                jpaEntity.getTelemetryId(),
                jpaEntity.getTimestamp(),
                jpaEntity.getValue(),
                jpaEntity.getRawPayload(),
                jpaEntity.getSensor() != null ? SensorMapper.toDomain(jpaEntity.getSensor()) : null
        );
    }
}
