package henrique.igor.iiot.infrastructure.web.specs;

import henrique.igor.iiot.domain.entities.enums.AlertStatus;
import henrique.igor.iiot.infrastructure.web.dtos.alert.AcknowledgeAlertRequest;
import henrique.igor.iiot.infrastructure.web.dtos.alert.AlertResponse;
import henrique.igor.iiot.infrastructure.web.dtos.alert.TriggerAlertRequest;
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

@Tag(name = "Alerts", description = "Endpoints for listing, triggering, acknowledging, and dismissing multi-sensor anomaly Alerts")
public interface AlertControllerSpec {

    @Operation(
            summary = "List anomaly Alerts",
            description = "Retrieves all anomaly Alerts (including their multi-sensor snapshots), optionally filtered by equipment ID and/or alert status."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of alerts retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = AlertResponse.class))
                    )
            )
    })
    ResponseEntity<List<AlertResponse>> listAll(
            @Parameter(description = "Optional equipment UUID filter")
            UUID equipmentId,
            @Parameter(description = "Optional alert status filter (PENDING, ACKNOWLEDGED, RESOLVED)")
            AlertStatus status
    );

    @Operation(
            summary = "Trigger a new anomaly Alert",
            description = "Triggers a new anomaly Alert for an equipment and rule group, recording multi-sensor reading snapshots at trigger time."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Alert triggered successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlertResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body or domain validation error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Equipment, Rule Group, or Sensor not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Rule Group or Sensor does not belong to the specified equipment",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    ResponseEntity<AlertResponse> trigger(TriggerAlertRequest request);

    @Operation(
            summary = "Acknowledge an Alert",
            description = "Marks a pending anomaly Alert as ACKNOWLEDGED by a specific operator/technician."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Alert acknowledged successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlertResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body or validation error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Alert or user not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Cannot acknowledge an already resolved alert",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    ResponseEntity<AlertResponse> acknowledge(
            @Parameter(description = "UUID of the Alert to acknowledge", required = true)
            UUID id,
            AcknowledgeAlertRequest request
    );

    @Operation(
            summary = "Dismiss / Resolve an Alert",
            description = "Transitions an anomaly Alert to RESOLVED status and records its resolution timestamp."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Alert resolved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlertResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid Alert ID supplied",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Alert not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Alert is already resolved",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    ResponseEntity<AlertResponse> dismiss(
            @Parameter(description = "UUID of the Alert to dismiss/resolve", required = true)
            UUID id
    );
}
