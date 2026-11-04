package henrique.igor.iiot.infrastructure.web.controllers;

import henrique.igor.iiot.application.usecases.alert.AcknowledgeAlertUseCase;
import henrique.igor.iiot.application.usecases.alert.DismissAlertUseCase;
import henrique.igor.iiot.application.usecases.alert.ListAlertsUseCase;
import henrique.igor.iiot.application.usecases.alert.TriggerAlertUseCase;
import henrique.igor.iiot.domain.entities.Alert;
import henrique.igor.iiot.domain.entities.enums.AlertStatus;
import henrique.igor.iiot.infrastructure.web.dtos.alert.AcknowledgeAlertRequest;
import henrique.igor.iiot.infrastructure.web.dtos.alert.AlertResponse;
import henrique.igor.iiot.infrastructure.web.dtos.alert.TriggerAlertRequest;
import henrique.igor.iiot.infrastructure.web.specs.AlertControllerSpec;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/alerts")
public class AlertController implements AlertControllerSpec {

    private final ListAlertsUseCase listAlertsUseCase;
    private final TriggerAlertUseCase triggerAlertUseCase;
    private final AcknowledgeAlertUseCase acknowledgeAlertUseCase;
    private final DismissAlertUseCase dismissAlertUseCase;

    public AlertController(
            ListAlertsUseCase listAlertsUseCase,
            TriggerAlertUseCase triggerAlertUseCase,
            AcknowledgeAlertUseCase acknowledgeAlertUseCase,
            DismissAlertUseCase dismissAlertUseCase
    ) {
        this.listAlertsUseCase = listAlertsUseCase;
        this.triggerAlertUseCase = triggerAlertUseCase;
        this.acknowledgeAlertUseCase = acknowledgeAlertUseCase;
        this.dismissAlertUseCase = dismissAlertUseCase;
    }

    @Override
    @GetMapping
    public ResponseEntity<List<AlertResponse>> listAll(
            @RequestParam(required = false) UUID equipmentId,
            @RequestParam(required = false) AlertStatus status
    ) {
        List<AlertResponse> alerts = listAlertsUseCase.execute(equipmentId, status).stream()
                .map(AlertResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(alerts);
    }

    @Override
    @PostMapping
    public ResponseEntity<AlertResponse> trigger(@Valid @RequestBody TriggerAlertRequest request) {
        Alert triggeredAlert = triggerAlertUseCase.execute(request.toInput());
        return ResponseEntity.status(HttpStatus.CREATED).body(AlertResponse.fromDomain(triggeredAlert));
    }

    @Override
    @PatchMapping("/{id}/acknowledge")
    public ResponseEntity<AlertResponse> acknowledge(
            @PathVariable UUID id,
            @Valid @RequestBody AcknowledgeAlertRequest request
    ) {
        Alert acknowledgedAlert = acknowledgeAlertUseCase.execute(request.toInput(id));
        return ResponseEntity.ok(AlertResponse.fromDomain(acknowledgedAlert));
    }

    @Override
    @PatchMapping("/{id}/dismiss")
    public ResponseEntity<AlertResponse> dismiss(@PathVariable UUID id) {
        Alert dismissedAlert = dismissAlertUseCase.execute(id);
        return ResponseEntity.ok(AlertResponse.fromDomain(dismissedAlert));
    }
}
