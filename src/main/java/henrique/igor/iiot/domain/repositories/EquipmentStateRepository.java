package henrique.igor.iiot.domain.repositories;

import henrique.igor.iiot.domain.entities.EquipmentState;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EquipmentStateRepository {

    EquipmentState save(EquipmentState equipmentState);

    Optional<EquipmentState> findByEquipmentId(UUID equipmentId);

    List<EquipmentState> findAll();

    void deleteByEquipmentId(UUID equipmentId);
}
