package henrique.igor.iiot.infrastructure.persistence.adapters;

import henrique.igor.iiot.domain.entities.Sector;
import henrique.igor.iiot.domain.repositories.SectorRepository;
import henrique.igor.iiot.infrastructure.persistence.entities.SectorJpaEntity;
import henrique.igor.iiot.infrastructure.persistence.mappers.SectorMapper;
import henrique.igor.iiot.infrastructure.persistence.repositories.jpa.SectorJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SectorPersistenceAdapter implements SectorRepository {

    private final SectorJpaRepository sectorJpaRepository;

    public SectorPersistenceAdapter(SectorJpaRepository sectorJpaRepository) {
        this.sectorJpaRepository = sectorJpaRepository;
    }

    @Override
    public Sector save(Sector sector) {
        SectorJpaEntity jpaEntity = SectorMapper.toJpaEntity(sector);
        SectorJpaEntity savedEntity = sectorJpaRepository.save(jpaEntity);
        return SectorMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Sector> findById(UUID id) {
        return sectorJpaRepository.findById(id)
                .map(SectorMapper::toDomain);
    }

    @Override
    public Optional<Sector> findByName(String name) {
        return sectorJpaRepository.findByName(name)
                .map(SectorMapper::toDomain);
    }

    @Override
    public List<Sector> findAll() {
        return sectorJpaRepository.findAll().stream()
                .map(SectorMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        sectorJpaRepository.deleteById(id);
    }
}
