package henrique.igor.iiot.domain.repositories;

import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.enums.EquipType;

import java.util.*;

public interface EquipmentRepository {

    Equipment save(Equipment equipment);

    Optional<Equipment> findById(UUID equipmentId);

    Optional<Equipment> findByEquipCode(String equipCode);

    long countBySectorIdAndType(UUID sectorId, EquipType type);

    long countBySectorIdIsNullAndType(EquipType type);

    List<Equipment> findAll();

    void deleteById(UUID equipmentId);
}
