package henrique.igor.iiot.infrastructure.persistence.repositories.jpa;

import henrique.igor.iiot.infrastructure.persistence.entities.ActuatorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ActuatorJpaRepository extends JpaRepository<ActuatorJpaEntity, UUID> {

    Optional<ActuatorJpaEntity> findByCode(String code);
}
