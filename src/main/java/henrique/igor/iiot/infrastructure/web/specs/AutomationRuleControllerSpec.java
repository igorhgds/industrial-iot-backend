package henrique.igor.iiot.infrastructure.web.specs;

import henrique.igor.iiot.application.usecases.automation.dto.ActuationCommandResult;
import henrique.igor.iiot.infrastructure.web.dtos.automation.AutomationRuleResponse;
import henrique.igor.iiot.infrastructure.web.dtos.automation.CreateAutomationRuleRequest;
import henrique.igor.iiot.infrastructure.web.dtos.automation.ExecuteActuationCommandRequest;
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

@Tag(name = "Automation Rules", description = "Endpoints for managing closed-loop automation rules and dispatching MQTT actuation commands")
public interface AutomationRuleControllerSpec {

    @Operation(
            summary = "Create a new Automation Rule",
            description = "Creates a closed-loop automation rule linking an input sensor, a rule condition, and a target actuator."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Automation Rule created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AutomationRuleResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body or domain validation error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sensor, rule condition, or target actuator not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Rule condition does not belong to the specified input sensor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    ResponseEntity<AutomationRuleResponse> create(CreateAutomationRuleRequest request);

    @Operation(
            summary = "Toggle an Automation Rule",
            description = "Toggles the active state (enabled/disabled) of an existing Automation Rule."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Automation Rule toggled successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AutomationRuleResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid Automation Rule ID supplied",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Automation Rule not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    ResponseEntity<AutomationRuleResponse> toggle(
            @Parameter(description = "UUID of the Automation Rule to toggle", required = true)
            UUID id
    );

    @Operation(
            summary = "Execute an actuation command",
            description = "Publishes an actuation command to the target actuator's MQTT command topic for an active Automation Rule."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Actuation command published successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActuationCommandResult.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid Automation Rule ID supplied",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Automation Rule not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Automation Rule is inactive",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Target actuator is offline or lacks a valid command MQTT topic",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    ResponseEntity<ActuationCommandResult> executeCommand(
            @Parameter(description = "UUID of the Automation Rule to execute", required = true)
            UUID id,
            ExecuteActuationCommandRequest request
    );
}
