package henrique.igor.iiot.infrastructure.web.specs;

import henrique.igor.iiot.infrastructure.web.dtos.sensor.RegisterSensorRequest;
import henrique.igor.iiot.infrastructure.web.dtos.sensor.SensorResponse;
import henrique.igor.iiot.infrastructure.web.dtos.sensor.UpdateSensorRequest;
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

@Tag(name = "Sensors", description = "Endpoints for registering and managing IoT sensors attached to equipment")
public interface SensorControllerSpec {

    @Operation(
            summary = "Register a new sensor",
            description = "Registers a new IoT sensor (optionally generating its code automatically) and attaches it to an equipment."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Sensor registered successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SensorResponse.class))
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
                    description = "Sensor with the given code already exists",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    ResponseEntity<SensorResponse> register(RegisterSensorRequest request);

    @Operation(
            summary = "List sensors by equipment",
            description = "Retrieves all IoT sensors attached to a specific industrial equipment."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of sensors retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = SensorResponse.class))
                    )
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
    ResponseEntity<List<SensorResponse>> listByEquipment(
            @Parameter(description = "UUID of the equipment", required = true)
            UUID equipmentId
    );

    @Operation(
            summary = "Update an existing sensor",
            description = "Updates operational status, MQTT topic, or equipment allocation (regenerating sensor code if relocated)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sensor updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SensorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body or validation error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sensor or referenced equipment not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    ResponseEntity<SensorResponse> update(
            @Parameter(description = "UUID of the sensor to update", required = true)
            UUID id,
            UpdateSensorRequest request
    );
}
