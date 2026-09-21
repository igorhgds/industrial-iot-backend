package henrique.igor.iiot.infrastructure.persistence.adapters;

import henrique.igor.iiot.domain.entities.TelemetryData;
import henrique.igor.iiot.domain.repositories.TelemetryDataRepository;
import henrique.igor.iiot.infrastructure.persistence.entities.TelemetryDataJpaEntity;
import henrique.igor.iiot.infrastructure.persistence.mappers.TelemetryDataMapper;
import henrique.igor.iiot.infrastructure.persistence.repositories.jpa.TelemetryDataJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TelemetryDataPersistenceAdapter implements TelemetryDataRepository {

    private final TelemetryDataJpaRepository telemetryDataJpaRepository;

    public TelemetryDataPersistenceAdapter(TelemetryDataJpaRepository telemetryDataJpaRepository) {
        this.telemetryDataJpaRepository = telemetryDataJpaRepository;
    }

    @Override
    public TelemetryData save(TelemetryData telemetryData) {
        TelemetryDataJpaEntity jpaEntity = TelemetryDataMapper.toJpaEntity(telemetryData);
        TelemetryDataJpaEntity savedEntity = telemetryDataJpaRepository.save(jpaEntity);
        return TelemetryDataMapper.toDomain(savedEntity);
    }

    @Override
    public List<TelemetryData> findBySensorIdAndTimestampBetween(UUID sensorId, OffsetDateTime start, OffsetDateTime end) {
        return telemetryDataJpaRepository.findByIdSensorIdAndIdTimestampBetween(sensorId, start, end)
                .stream()
                .map(TelemetryDataMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<TelemetryData> findLatestBySensorId(UUID sensorId) {
        return telemetryDataJpaRepository.findFirstByIdSensorIdByIdTimestampDesc(sensorId)
                .map(TelemetryDataMapper::toDomain);
    }
}
