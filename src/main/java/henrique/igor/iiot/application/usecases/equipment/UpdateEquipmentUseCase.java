package henrique.igor.iiot.application.usecases.equipment;

import henrique.igor.iiot.application.usecases.equipment.dto.UpdateEquipmentInput;
import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.Gateway;
import henrique.igor.iiot.domain.entities.Sector;
import henrique.igor.iiot.domain.repositories.EquipmentRepository;
import henrique.igor.iiot.domain.repositories.GatewayRepository;
import henrique.igor.iiot.domain.repositories.SectorRepository;

public class UpdateEquipmentUseCase {

    private final EquipmentRepository equipmentRepository;
    private final SectorRepository sectorRepository;
    private final GatewayRepository gatewayRepository;

    public UpdateEquipmentUseCase(EquipmentRepository equipmentRepository, SectorRepository sectorRepository, GatewayRepository gatewayRepository) {
        this.equipmentRepository = equipmentRepository;
        this.sectorRepository = sectorRepository;
        this.gatewayRepository = gatewayRepository;
    }

    public Equipment execute(UpdateEquipmentInput input) {
        Equipment equipment = equipmentRepository.findById(input.equipmentId())
                .orElseThrow(() -> new IllegalArgumentException("Equipment not found with ID: " + input.equipmentId()));

        if (input.status() != null) {
            equipment.changeStatus(input.status());
        }

        if (input.sectorId() != null) {
            Sector newSector = sectorRepository.findById(input.sectorId())
                    .orElseThrow(() -> new IllegalArgumentException("Sector not found with ID: " + input.sectorId()));
            equipment.relocateToSector(newSector);
        }

        if (input.gatewayId() != null) {
            Gateway newGateway = gatewayRepository.findById(input.gatewayId())
                    .orElseThrow(() -> new IllegalArgumentException("Gateway not found with ID: " + input.gatewayId()));
            equipment.connectToGateway(newGateway);
        }

        return equipmentRepository.save(equipment);
    }
}
