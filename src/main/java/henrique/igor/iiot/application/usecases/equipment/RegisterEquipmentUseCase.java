package henrique.igor.iiot.application.usecases.equipment;

import henrique.igor.iiot.application.usecases.equipment.dto.RegisterEquipmentRequest;
import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.Gateway;
import henrique.igor.iiot.domain.entities.Sector;
import henrique.igor.iiot.domain.entities.enums.EquipStatus;
import henrique.igor.iiot.domain.repositories.EquipmentRepository;
import henrique.igor.iiot.domain.repositories.GatewayRepository;
import henrique.igor.iiot.domain.repositories.SectorRepository;
import henrique.igor.iiot.domain.services.EquipmentCodeGenerator;

public class RegisterEquipmentUseCase {

    private final EquipmentRepository equipmentRepository;
    private final SectorRepository sectorRepository;
    private final GatewayRepository gatewayRepository;
    private final EquipmentCodeGenerator equipmentCodeGenerator;

    public RegisterEquipmentUseCase(EquipmentRepository equipmentRepository,
                                  SectorRepository sectorRepository,
                                  GatewayRepository gatewayRepository,
                                  EquipmentCodeGenerator equipmentCodeGenerator) {
        this.equipmentRepository = equipmentRepository;
        this.sectorRepository = sectorRepository;
        this.gatewayRepository = gatewayRepository;
        this.equipmentCodeGenerator = equipmentCodeGenerator;
    }

    public Equipment execute(RegisterEquipmentRequest request) {
        if (request.type() == null) {
            throw new IllegalArgumentException("Equipment type cannot be null.");
        }

        Sector sector = null;
        if (request.sectorId() != null) {
            sector = sectorRepository.findById(request.sectorId())
                    .orElseThrow(() -> new IllegalArgumentException("Sector not found with ID: " + request.sectorId()));
        }

        Gateway gateway = null;
        if (request.gatewayId() != null) {
            gateway = gatewayRepository.findById(request.gatewayId())
                    .orElseThrow(() -> new IllegalArgumentException("Gateway not found with ID: " + request.gatewayId()));
        }

        String equipCode = request.equipCode();
        if (equipCode == null || equipCode.isBlank()) {
            equipCode = equipmentCodeGenerator.generate(request.type(), sector);
        } else {
            if (equipmentRepository.findByEquipCode(equipCode).isPresent()) {
                throw new IllegalArgumentException("Equipment with code " + equipCode + " already exists.");
            }
        }

        EquipStatus status = request.status() != null ? request.status() : EquipStatus.ACTIVE;

        Equipment newEquipment = new Equipment(
                equipCode,
                request.type(),
                sector,
                gateway
        );
        newEquipment.changeStatus(status);

        return equipmentRepository.save(newEquipment);
    }
}
