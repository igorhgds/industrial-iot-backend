package henrique.igor.iiot.infrastructure.persistence.repositories.jpa;

import henrique.igor.iiot.infrastructure.persistence.entities.SensorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SensorJpaRepository extends JpaRepository<SensorJpaEntity, UUID> {

    Optional<SensorJpaEntity> findByCode(String code);
}
