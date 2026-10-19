package henrique.igor.iiot.application.usecases.equipment;

import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.EquipmentRepository;

import java.util.UUID;

public class GetEquipmentDetailsUseCase {

    private final EquipmentRepository equipmentRepository;

    public GetEquipmentDetailsUseCase(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    public Equipment execute(UUID equipmentId) {
        if (equipmentId == null) {
            throw new DomainValidationException("Equipment ID cannot be null.");
        }

        return equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", equipmentId));
    }
}
