package henrique.igor.iiot.infrastructure.web.specs;

import henrique.igor.iiot.infrastructure.web.dtos.equipment.EquipmentResponse;
import henrique.igor.iiot.infrastructure.web.dtos.equipment.RegisterEquipmentRequest;
import henrique.igor.iiot.infrastructure.web.dtos.equipment.UpdateEquipmentRequest;
import henrique.igor.iiot.infrastructure.web.exception.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Equipments", description = "Endpoints for registering and managing industrial machinery and equipment")
public interface EquipmentControllerSpec {

    @Operation(
            summary = "Register a new equipment",
            description = "Registers a new industrial equipment (optionally generating its equipCode automatically) and links it to a sector and gateway."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Equipment registered successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EquipmentResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body or domain validation error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Referenced sector or gateway not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Equipment with the given code already exists",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    ResponseEntity<EquipmentResponse> register(RegisterEquipmentRequest request);

    @Operation(summary = "Get equipment details by ID", description = "Retrieves detailed configuration information of a specific equipment.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Equipment found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EquipmentResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid equipment ID supplied",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Equipment not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    ResponseEntity<EquipmentResponse> getDetails(
            @Parameter(description = "UUID of the equipment to retrieve", required = true)
            UUID id
    );

    @Operation(
            summary = "Update an existing equipment",
            description = "Updates operational status, sector allocation, or connected gateway of an existing equipment."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Equipment updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EquipmentResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body or validation error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Equipment, sector, or gateway not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    ResponseEntity<EquipmentResponse> update(
            @Parameter(description = "UUID of the equipment to update", required = true)
            UUID id,
            UpdateEquipmentRequest request
    );
}
