package henrique.igor.iiot.infrastructure.web.dtos.sector;

import henrique.igor.iiot.domain.entities.Sector;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Industrial sector representation returned by the API")
public record SectorResponse(
        @Schema(description = "Unique identifier of the sector", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID sectorId,

        @Schema(description = "Name of the sector", example = "Usinagem")
        String name,

        @Schema(description = "Description of the sector", example = "Setor de usinagem CNC e tornos mecânicos")
        String description,

        @Schema(description = "Timestamp when the sector was created", example = "2026-10-06T12:00:00Z")
        OffsetDateTime createdAt
) {
    public static SectorResponse fromDomain(Sector sector) {
        return new SectorResponse(
                sector.getSectorId(),
                sector.getName(),
                sector.getDescription(),
                sector.getCreatedAt()
        );
    }
}
