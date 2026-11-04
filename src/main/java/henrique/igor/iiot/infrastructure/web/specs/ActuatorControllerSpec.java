package henrique.igor.iiot.infrastructure.web.specs;

import henrique.igor.iiot.infrastructure.web.dtos.actuator.ActuatorResponse;
import henrique.igor.iiot.infrastructure.web.dtos.actuator.RegisterActuatorRequest;
import henrique.igor.iiot.infrastructure.web.dtos.actuator.UpdateActuatorRequest;
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

@Tag(name = "Actuators", description = "Endpoints for registering and managing IoT actuators attached to equipment")
public interface ActuatorControllerSpec {

    @Operation(
            summary = "Register a new actuator",
            description = "Registers a new IoT actuator (optionally generating its code automatically) and links it to an equipment."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Actuator registered successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActuatorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body or domain validation error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Referenced equipment not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Actuator with the given code already exists",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    ResponseEntity<ActuatorResponse> register(RegisterActuatorRequest request);

    @Operation(
            summary = "Update an existing actuator",
            description = "Updates operational status, MQTT command/state topics, or equipment allocation (regenerating actuator code if relocated)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Actuator updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActuatorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body or validation error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Actuator or referenced equipment not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    ResponseEntity<ActuatorResponse> update(
            @Parameter(description = "UUID of the actuator to update", required = true)
            UUID id,
            UpdateActuatorRequest request
    );
}
