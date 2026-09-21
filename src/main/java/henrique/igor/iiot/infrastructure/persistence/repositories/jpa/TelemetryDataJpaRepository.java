package henrique.igor.iiot.infrastructure.persistence.repositories.jpa;

import henrique.igor.iiot.infrastructure.persistence.entities.TelemetryDataId;
import henrique.igor.iiot.infrastructure.persistence.entities.TelemetryDataJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TelemetryDataJpaRepository extends JpaRepository<TelemetryDataJpaEntity, TelemetryDataId> {

    List<TelemetryDataJpaEntity> findByIdSensorIdAndIdTimestampBetween(UUID sensorId, OffsetDateTime start, OffsetDateTime end);

    Optional<TelemetryDataJpaEntity> findFirstByIdSensorIdByIdTimestampDesc(UUID sensorId);
}
