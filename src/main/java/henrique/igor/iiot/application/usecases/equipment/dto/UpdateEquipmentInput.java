package henrique.igor.iiot.application.usecases.equipment.dto;

import henrique.igor.iiot.domain.entities.enums.EquipStatus;

import java.util.UUID;

public record UpdateEquipmentInput(
        UUID equipmentId,
        EquipStatus status,
        UUID sectorId,
        UUID gatewayId
) {
}
