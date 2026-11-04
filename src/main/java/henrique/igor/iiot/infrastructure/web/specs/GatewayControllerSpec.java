package henrique.igor.iiot.infrastructure.web.specs;

import henrique.igor.iiot.infrastructure.web.dtos.gateway.GatewayResponse;
import henrique.igor.iiot.infrastructure.web.dtos.gateway.RegisterGatewayRequest;
import henrique.igor.iiot.infrastructure.web.dtos.gateway.UpdateGatewayRequest;
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

@Tag(name = "Gateways", description = "Endpoints for registering and managing IoT edge gateways")
public interface GatewayControllerSpec {

    @Operation(
            summary = "Register a new gateway",
            description = "Registers a new IoT edge gateway (optionally generating its code automatically) and allocates it to a sector."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Gateway registered successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GatewayResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body or validation error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Referenced sector not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Gateway with the given code already exists",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    ResponseEntity<GatewayResponse> register(RegisterGatewayRequest request);

    @Operation(
            summary = "List gateways by sector",
            description = "Retrieves all IoT edge gateways installed in a specific industrial sector."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of gateways retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = GatewayResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid sector ID supplied",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sector not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    ResponseEntity<List<GatewayResponse>> listBySector(
            @Parameter(description = "UUID of the sector", required = true)
            UUID sectorId
    );

    @Operation(
            summary = "Update an existing gateway",
            description = "Updates network configuration, firmware version, operational status, or sector allocation of an existing gateway."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Gateway updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GatewayResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body or validation error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Gateway or referenced sector not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    ResponseEntity<GatewayResponse> update(
            @Parameter(description = "UUID of the gateway to update", required = true)
            UUID id,
            UpdateGatewayRequest request
    );
}
