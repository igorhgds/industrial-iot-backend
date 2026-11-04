package henrique.igor.iiot.application.usecases.equipmentState;

import henrique.igor.iiot.domain.entities.EquipmentState;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.EquipmentStateRepository;

import java.util.UUID;

public class GetEquipmentStateUseCase {

    private final EquipmentStateRepository equipmentStateRepository;

    public GetEquipmentStateUseCase(EquipmentStateRepository equipmentStateRepository) {
        this.equipmentStateRepository = equipmentStateRepository;
    }

    public EquipmentState execute(UUID equipmentId) {
        if (equipmentId == null) {
            throw new DomainValidationException("Equipment ID cannot be null.");
        }

        return equipmentStateRepository.findByEquipmentId(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("EquipmentState", equipmentId));
    }
}
