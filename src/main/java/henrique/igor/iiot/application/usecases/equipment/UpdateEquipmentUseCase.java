package henrique.igor.iiot.application.usecases.equipment;

import henrique.igor.iiot.application.usecases.equipment.dto.UpdateEquipmentRequest;
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

    public Equipment execute(UpdateEquipmentRequest request) {
        Equipment equipment = equipmentRepository.findById(request.equipmentId())
                .orElseThrow(() -> new IllegalArgumentException("Equipment not found with ID: " + request.equipmentId()));

        if (request.status() != null) {
            equipment.changeStatus(request.status());
        }

        if (request.sectorId() != null) {
            Sector newSector = sectorRepository.findById(request.sectorId())
                    .orElseThrow(() -> new IllegalArgumentException("Sector not found with ID: " + request.sectorId()));
            equipment.relocateToSector(newSector);
        }

        if (request.gatewayId() != null) {
            Gateway newGateway = gatewayRepository.findById(request.gatewayId())
                    .orElseThrow(() -> new IllegalArgumentException("Gateway not found with ID: " + request.gatewayId()));
            equipment.connectToGateway(newGateway);
        }

        return equipmentRepository.save(equipment);
    }
}
