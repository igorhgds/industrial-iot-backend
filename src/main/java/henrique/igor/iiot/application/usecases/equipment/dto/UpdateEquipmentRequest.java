package henrique.igor.iiot.application.usecases.equipment.dto;

import henrique.igor.iiot.domain.entities.enums.EquipStatus;
import henrique.igor.iiot.domain.entities.enums.EquipType;

import java.util.UUID;

public record UpdateEquipmentRequest(
        UUID equipmentId,
        EquipStatus status,
        UUID sectorId,
        UUID gatewayId
) {
}
