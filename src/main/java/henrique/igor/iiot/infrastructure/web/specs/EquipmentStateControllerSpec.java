package henrique.igor.iiot.infrastructure.web.specs;

import henrique.igor.iiot.infrastructure.web.dtos.equipmentState.EquipmentStateResponse;
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

@Tag(name = "Equipment States", description = "Endpoints for querying O(1) current health state and latest telemetry snapshot of equipment")
public interface EquipmentStateControllerSpec {

    @Operation(
            summary = "List all equipment states",
            description = "Retrieves the current health state and latest telemetry snapshot of all monitored industrial equipment."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of equipment states retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = EquipmentStateResponse.class))
                    )
            )
    })
    ResponseEntity<List<EquipmentStateResponse>> listAll();

    @Operation(
            summary = "Get equipment state by equipment ID",
            description = "Retrieves the O(1) current health status and latest raw telemetry payload of a specific equipment."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Equipment state found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EquipmentStateResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid equipment ID supplied",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Equipment state not found for the given equipment ID",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    ResponseEntity<EquipmentStateResponse> getByEquipmentId(
            @Parameter(description = "UUID of the equipment", required = true)
            UUID equipmentId
    );
}
