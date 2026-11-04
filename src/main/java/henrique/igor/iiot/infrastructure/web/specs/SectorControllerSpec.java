package henrique.igor.iiot.infrastructure.web.specs;

import henrique.igor.iiot.infrastructure.web.dtos.sector.CreateSectorRequest;
import henrique.igor.iiot.infrastructure.web.dtos.sector.SectorResponse;
import henrique.igor.iiot.infrastructure.web.dtos.sector.UpdateSectorRequest;
import henrique.igor.iiot.infrastructure.web.exception.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "Sectors", description = "Endpoints for managing industrial plant sectors")
public interface SectorControllerSpec {

    @Operation(summary = "Create a new sector", description = "Registers a new industrial sector in the plant.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Sector created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SectorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body or validation error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Sector with the given name already exists",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    ResponseEntity<SectorResponse> create(CreateSectorRequest request);

    @Operation(summary = "List all sectors", description = "Retrieves all registered industrial sectors.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of sectors retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = SectorResponse.class))
                    )
            )
    })
    ResponseEntity<List<SectorResponse>> listAll();

    @Operation(summary = "Update an existing sector", description = "Updates the name and description of an existing sector.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sector updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SectorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body or validation error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sector not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Sector name already in use by another sector",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    ResponseEntity<SectorResponse> update(
            @Parameter(description = "UUID of the sector to update", required = true)
            UUID id,
            UpdateSectorRequest request
    );
}
