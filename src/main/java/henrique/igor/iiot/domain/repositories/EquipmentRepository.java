package henrique.igor.iiot.domain.repositories;

import henrique.igor.iiot.domain.entities.Equipment;

import java.util.*;

public interface EquipmentRepository {

    Equipment save(Equipment equipment);

    Optional<Equipment> findById(UUID equipmentId);

    Optional<Equipment> findByEquipCode(String equipCode);

    List<Equipment> findAll();

    void deleteById(UUID equipmentId);
}
