package henrique.igor.iiot.domain.repositories;

import henrique.igor.iiot.domain.entities.TelemetryData;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TelemetryDataRepository {

    TelemetryData save(TelemetryData telemetryData);

    List<TelemetryData> findBySensorIdAndTimestampBetween(UUID sensorId, OffsetDateTime start, OffsetDateTime end);

    Optional<TelemetryData> findLatestBySensorId(UUID sensorId);
}
