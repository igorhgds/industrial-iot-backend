package henrique.igor.iiot.application.usecases.sensor;

import henrique.igor.iiot.domain.entities.Sensor;
import henrique.igor.iiot.domain.repositories.EquipmentRepository;
import henrique.igor.iiot.domain.repositories.SensorRepository;

import java.util.List;
import java.util.UUID;

public class ListSensorByEquipmentUseCase {

    private final SensorRepository sensorRepository;
    private final EquipmentRepository equipmentRepository;

    public ListSensorByEquipmentUseCase(SensorRepository sensorRepository, EquipmentRepository equipmentRepository) {
        this.sensorRepository = sensorRepository;
        this.equipmentRepository = equipmentRepository;
    }

    public List<Sensor> execute(UUID equipmentId){
        if (equipmentId == null){
            throw new IllegalArgumentException("Equipment ID cannot be null.");
        }

        equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Equipment not found with ID: " + equipmentId));

        return sensorRepository.findByEquipmentId(equipmentId);
    }
}