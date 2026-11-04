package henrique.igor.iiot.application.usecases.equipmentState;

import henrique.igor.iiot.domain.entities.EquipmentState;
import henrique.igor.iiot.domain.repositories.EquipmentStateRepository;

import java.util.List;

public class ListEquipmentStatesUseCase {

    private final EquipmentStateRepository equipmentStateRepository;

    public ListEquipmentStatesUseCase(EquipmentStateRepository equipmentStateRepository) {
        this.equipmentStateRepository = equipmentStateRepository;
    }

    public List<EquipmentState> execute() {
        return equipmentStateRepository.findAll();
    }
}
