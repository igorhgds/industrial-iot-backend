package henrique.igor.iiot.infrastructure.web.specs;

import henrique.igor.iiot.infrastructure.web.dtos.ruleGroup.AddConditionToRuleGroupRequest;
import henrique.igor.iiot.infrastructure.web.dtos.ruleGroup.CreateRuleGroupRequest;
import henrique.igor.iiot.infrastructure.web.dtos.ruleGroup.RuleConditionResponse;
import henrique.igor.iiot.infrastructure.web.dtos.ruleGroup.RuleGroupResponse;
import henrique.igor.iiot.infrastructure.web.dtos.ruleGroup.UpdateRuleGroupRequest;
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

@Tag(name = "Rule Groups", description = "Endpoints for managing dynamic anomaly detection Rule Groups and threshold conditions")
public interface RuleGroupControllerSpec {

    @Operation(
            summary = "Create a new Rule Group",
            description = "Creates a new inactive dynamic Rule Group for an equipment (must add at least one condition before activation)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Rule Group created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RuleGroupResponse.class))
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
                    description = "Rule Group with the same name already exists for this equipment",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    ResponseEntity<RuleGroupResponse> create(CreateRuleGroupRequest request);

    @Operation(
            summary = "List Rule Groups by equipment",
            description = "Retrieves all dynamic Rule Groups configured for a specific industrial equipment."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of Rule Groups retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = RuleGroupResponse.class))
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
    ResponseEntity<List<RuleGroupResponse>> listByEquipment(
            @Parameter(description = "UUID of the equipment", required = true)
            UUID equipmentId
    );

    @Operation(
            summary = "Update an existing Rule Group",
            description = "Updates name, description, severity, logical operator, or active status of a Rule Group."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Rule Group updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RuleGroupResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body or validation error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Rule Group not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Duplicate Rule Group name on equipment or attempt to activate without conditions",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    ResponseEntity<RuleGroupResponse> update(
            @Parameter(description = "UUID of the Rule Group to update", required = true)
            UUID id,
            UpdateRuleGroupRequest request
    );

    @Operation(
            summary = "Activate a Rule Group",
            description = "Activates a Rule Group so it is evaluated on incoming telemetry streams (requires at least one condition registered)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Rule Group activated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RuleGroupResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid Rule Group ID supplied",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Rule Group not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Cannot activate a Rule Group without at least one condition",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    ResponseEntity<RuleGroupResponse> activate(
            @Parameter(description = "UUID of the Rule Group to activate", required = true)
            UUID id
    );

    @Operation(
            summary = "Add a sensor threshold condition to a Rule Group",
            description = "Adds a comparison threshold condition for a sensor belonging to the same equipment as the Rule Group."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Condition added to Rule Group successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RuleConditionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body or domain validation error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Rule Group or sensor not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Sensor does not belong to the same equipment as the Rule Group",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    ResponseEntity<RuleConditionResponse> addCondition(
            @Parameter(description = "UUID of the Rule Group", required = true)
            UUID id,
            AddConditionToRuleGroupRequest request
    );
}
