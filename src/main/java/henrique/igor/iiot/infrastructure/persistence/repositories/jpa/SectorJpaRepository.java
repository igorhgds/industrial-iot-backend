package henrique.igor.iiot.infrastructure.persistence.repositories.jpa;

import henrique.igor.iiot.infrastructure.persistence.entities.SectorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SectorJpaRepository extends JpaRepository<SectorJpaEntity, UUID> {

    Optional<SectorJpaEntity> findByName(String name);
}
