package henrique.igor.iiot.infrastructure.web.dtos.sector;

import henrique.igor.iiot.application.usecases.sector.dto.UpdateSectorInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Payload for updating an existing industrial sector")
public record UpdateSectorRequest(
        @Schema(description = "Updated name of the sector", example = "Usinagem Pesada")
        @NotBlank(message = "Name is required")
        @Size(max = 50, message = "Name must not exceed 50 characters")
        String name,

        @Schema(description = "Updated description of the sector", example = "Setor de usinagem CNC de grande porte")
        String description
) {
    public UpdateSectorInput toInput(UUID sectorId) {
        return new UpdateSectorInput(sectorId, name, description);
    }
}
