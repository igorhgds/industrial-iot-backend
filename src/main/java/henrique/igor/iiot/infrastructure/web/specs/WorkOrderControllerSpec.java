package henrique.igor.iiot.infrastructure.web.specs;

import henrique.igor.iiot.application.usecases.workOrder.dto.WorkOrderSheetOutput;
import henrique.igor.iiot.infrastructure.web.dtos.workOrder.AssignWorkOrderRequest;
import henrique.igor.iiot.infrastructure.web.dtos.workOrder.CloseWorkOrderRequest;
import henrique.igor.iiot.infrastructure.web.dtos.workOrder.CreateWorkOrderRequest;
import henrique.igor.iiot.infrastructure.web.dtos.workOrder.UpdateWorkOrderStatusRequest;
import henrique.igor.iiot.infrastructure.web.dtos.workOrder.WorkOrderResponse;
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

@Tag(name = "Work Orders", description = "Endpoints for managing maintenance Work Orders (Ordens de Serviço), technician assignments, lifecycle transitions, and printable sheets")
public interface WorkOrderControllerSpec {

    @Operation(
            summary = "Create a new Work Order",
            description = "Creates a maintenance Work Order for an equipment, optionally linking an anomaly alert and assigning a technician."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Work Order created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = WorkOrderResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body or domain validation error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Equipment, alert, or assigned user not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Alert does not belong to the specified equipment or assigned user is not a TECHNICIAN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    ResponseEntity<WorkOrderResponse> create(CreateWorkOrderRequest request);

    @Operation(
            summary = "Assign a technician to a Work Order",
            description = "Assigns a user with the TECHNICIAN role to an open or in-progress Work Order."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Technician assigned successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = WorkOrderResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body or validation error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Work Order or user not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Cannot assign technician to a closed Work Order",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Assigned user does not have the TECHNICIAN role",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    ResponseEntity<WorkOrderResponse> assignTechnician(
            @Parameter(description = "UUID of the Work Order", required = true)
            UUID id,
            AssignWorkOrderRequest request
    );

    @Operation(
            summary = "Transition Work Order status",
            description = "Updates the lifecycle status of a Work Order enforcing state machine transitions (OPEN -> IN_PROGRESS/CANCELLED, IN_PROGRESS -> COMPLETED/CANCELLED)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Work Order status updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = WorkOrderResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body or validation error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Work Order not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Invalid Work Order state transition",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    ResponseEntity<WorkOrderResponse> updateStatus(
            @Parameter(description = "UUID of the Work Order", required = true)
            UUID id,
            UpdateWorkOrderStatusRequest request
    );

    @Operation(
            summary = "Close Work Order with resolution notes",
            description = "Completes a Work Order by recording technical resolution notes and automatically resolving the linked anomaly alert."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Work Order closed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = WorkOrderResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Missing resolution notes or validation error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Work Order not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Work Order is already closed",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    ResponseEntity<WorkOrderResponse> close(
            @Parameter(description = "UUID of the Work Order", required = true)
            UUID id,
            CloseWorkOrderRequest request
    );

    @Operation(
            summary = "Get printable Work Order sheet",
            description = "Retrieves the consolidated Work Order sheet including equipment details, technician info, linked alert, and sensor snapshots at trigger time."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Work Order sheet retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = WorkOrderSheetOutput.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid Work Order ID supplied",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Work Order not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    ResponseEntity<WorkOrderSheetOutput> getSheet(
            @Parameter(description = "UUID of the Work Order", required = true)
            UUID id
    );
}
