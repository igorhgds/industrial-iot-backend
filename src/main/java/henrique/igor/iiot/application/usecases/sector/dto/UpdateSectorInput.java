package henrique.igor.iiot.application.usecases.sector.dto;

import java.util.UUID;

public record UpdateSectorInput(
        UUID sectorId,
        String name,
        String description
) {
}
