package henrique.igor.iiot.infrastructure.web.dtos.sector;

import henrique.igor.iiot.application.usecases.sector.dto.CreateSectorInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload for creating a new industrial sector")
public record CreateSectorRequest(
        @Schema(description = "Unique name of the sector", example = "Usinagem")
        @NotBlank(message = "Name is required")
        @Size(max = 50, message = "Name must not exceed 50 characters")
        String name,

        @Schema(description = "Detailed description of the sector", example = "Setor de usinagem CNC e tornos mecânicos")
        String description
) {
    public CreateSectorInput toInput() {
        return new CreateSectorInput(name, description);
    }
}
