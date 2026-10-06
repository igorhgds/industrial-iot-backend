package henrique.igor.iiot.infrastructure.persistence.adapters;

import henrique.igor.iiot.domain.entities.EquipmentState;
import henrique.igor.iiot.domain.repositories.EquipmentStateRepository;
import henrique.igor.iiot.infrastructure.persistence.entities.EquipmentStateJpaEntity;
import henrique.igor.iiot.infrastructure.persistence.mappers.EquipmentStateMapper;
import henrique.igor.iiot.infrastructure.persistence.repositories.jpa.EquipmentStateJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class EquipmentStatePersistenceAdapter implements EquipmentStateRepository {

    private final EquipmentStateJpaRepository equipmentStateJpaRepository;

    public EquipmentStatePersistenceAdapter(EquipmentStateJpaRepository equipmentStateJpaRepository) {
        this.equipmentStateJpaRepository = equipmentStateJpaRepository;
    }

    @Override
    public EquipmentState save(EquipmentState equipmentState) {
        if (equipmentState == null || equipmentState.getEquipmentId() == null) {
            return null;
        }
        UUID id = equipmentState.getEquipmentId().getEquipmentId();
        EquipmentStateJpaEntity jpaEntity = equipmentStateJpaRepository.findById(id)
                .map(existing -> {
                    existing.setStatus(equipmentState.getStatus());
                    existing.setLastPayload(equipmentState.getLastPayload());
                    existing.setUpdatedAt(equipmentState.getUpdatedAt());
                    return existing;
                })
                .orElseGet(() -> EquipmentStateMapper.toJpaEntity(equipmentState));

        EquipmentStateJpaEntity savedEntity = equipmentStateJpaRepository.save(jpaEntity);
        return EquipmentStateMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<EquipmentState> findByEquipmentId(UUID equipmentId) {
        return equipmentStateJpaRepository.findById(equipmentId)
                .map(EquipmentStateMapper::toDomain);
    }

    @Override
    public List<EquipmentState> findAll() {
        return equipmentStateJpaRepository.findAll()
                .stream()
                .map(EquipmentStateMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteByEquipmentId(UUID equipmentId) {
        equipmentStateJpaRepository.deleteById(equipmentId);
    }
}
