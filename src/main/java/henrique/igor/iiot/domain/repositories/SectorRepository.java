package henrique.igor.iiot.domain.repositories;

import henrique.igor.iiot.domain.entities.Sector;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SectorRepository {

    Sector save(Sector sector);

    Optional<Sector> findById(UUID id);

    Optional<Sector> findByName(String name);

    List<Sector> findAll();

    void deleteById(UUID id);
}
