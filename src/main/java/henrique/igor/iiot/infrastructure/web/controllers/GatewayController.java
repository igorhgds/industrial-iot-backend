package henrique.igor.iiot.infrastructure.web.controllers;

import henrique.igor.iiot.application.usecases.gateway.ListGatewaysBySectorUseCase;
import henrique.igor.iiot.application.usecases.gateway.RegisterGatewayUseCase;
import henrique.igor.iiot.application.usecases.gateway.UpdateGatewayUseCase;
import henrique.igor.iiot.domain.entities.Gateway;
import henrique.igor.iiot.infrastructure.web.dtos.gateway.GatewayResponse;
import henrique.igor.iiot.infrastructure.web.dtos.gateway.RegisterGatewayRequest;
import henrique.igor.iiot.infrastructure.web.dtos.gateway.UpdateGatewayRequest;
import henrique.igor.iiot.infrastructure.web.specs.GatewayControllerSpec;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/gateways")
public class GatewayController implements GatewayControllerSpec {

    private final RegisterGatewayUseCase registerGatewayUseCase;
    private final ListGatewaysBySectorUseCase listGatewaysBySectorUseCase;
    private final UpdateGatewayUseCase updateGatewayUseCase;

    public GatewayController(
            RegisterGatewayUseCase registerGatewayUseCase,
            ListGatewaysBySectorUseCase listGatewaysBySectorUseCase,
            UpdateGatewayUseCase updateGatewayUseCase
    ) {
        this.registerGatewayUseCase = registerGatewayUseCase;
        this.listGatewaysBySectorUseCase = listGatewaysBySectorUseCase;
        this.updateGatewayUseCase = updateGatewayUseCase;
    }

    @Override
    @PostMapping
    public ResponseEntity<GatewayResponse> register(@Valid @RequestBody RegisterGatewayRequest request) {
        Gateway registeredGateway = registerGatewayUseCase.execute(request.toInput());
        return ResponseEntity.status(HttpStatus.CREATED).body(GatewayResponse.fromDomain(registeredGateway));
    }

    @Override
    @GetMapping("/sector/{sectorId}")
    public ResponseEntity<List<GatewayResponse>> listBySector(@PathVariable UUID sectorId) {
        List<GatewayResponse> gateways = listGatewaysBySectorUseCase.execute(sectorId).stream()
                .map(GatewayResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(gateways);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<GatewayResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateGatewayRequest request
    ) {
        Gateway updatedGateway = updateGatewayUseCase.execute(request.toInput(id));
        return ResponseEntity.ok(GatewayResponse.fromDomain(updatedGateway));
    }
}
