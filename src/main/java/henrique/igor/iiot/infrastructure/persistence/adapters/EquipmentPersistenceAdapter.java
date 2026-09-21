package henrique.igor.iiot.infrastructure.persistence.adapters;

import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.repositories.EquipmentRepository;
import henrique.igor.iiot.infrastructure.persistence.entities.EquipmentJpaEntity;
import henrique.igor.iiot.infrastructure.persistence.mappers.EquipmentMapper;
import henrique.igor.iiot.infrastructure.persistence.repositories.jpa.EquipmentJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class EquipmentPersistenceAdapter implements EquipmentRepository {

    private final EquipmentJpaRepository equipmentJpaRepository;

    public EquipmentPersistenceAdapter(EquipmentJpaRepository equipmentJpaRepository) {
        this.equipmentJpaRepository = equipmentJpaRepository;
    }


    @Override
    public Equipment save(Equipment equipment) {
        EquipmentJpaEntity jpaEntity = EquipmentMapper.toJpaEntity(equipment);
        EquipmentJpaEntity savedEntity = equipmentJpaRepository.save(jpaEntity);
        return EquipmentMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Equipment> findById(UUID equipmentId) {
        return equipmentJpaRepository.findById(equipmentId)
                .map(EquipmentMapper::toDomain);
    }

    @Override
    public Optional<Equipment> findByEquipCode(String equipCode) {
        return equipmentJpaRepository.findByEquipCode(equipCode)
                .map(EquipmentMapper::toDomain);
    }

    @Override
    public List<Equipment> findAll() {
        return equipmentJpaRepository.findAll()
                .stream()
                .map(EquipmentMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID equipmentId) {
        equipmentJpaRepository.deleteById(equipmentId);
    }
}
