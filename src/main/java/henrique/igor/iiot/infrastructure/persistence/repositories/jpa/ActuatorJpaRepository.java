package henrique.igor.iiot.infrastructure.persistence.repositories.jpa;

import henrique.igor.iiot.domain.entities.enums.ActuatorType;
import henrique.igor.iiot.infrastructure.persistence.entities.ActuatorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface ActuatorJpaRepository extends JpaRepository<ActuatorJpaEntity, UUID> {

    Optional<ActuatorJpaEntity> findByCode(String code);

    @Query("SELECT a.code FROM ActuatorJpaEntity a WHERE a.equipment.equipmentId = :equipmentId AND a.actuatorType = :type")
    List<String> findCodesByEquipmentIdAndType(
            @Param("equipmentId") UUID equipmentId,
            @Param("type") ActuatorType type
    );

    @Query("SELECT a.code FROM ActuatorJpaEntity a WHERE a.equipment IS NULL AND a.actuatorType = :type")
    List<String> findCodesByEquipmentIsNullAndType(
            @Param("type") ActuatorType type
    );
}
