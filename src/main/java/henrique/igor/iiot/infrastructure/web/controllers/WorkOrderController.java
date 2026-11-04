package henrique.igor.iiot.infrastructure.web.controllers;

import henrique.igor.iiot.application.usecases.workOrder.AssignWorkOrderUseCase;
import henrique.igor.iiot.application.usecases.workOrder.CloseWorkOrderUseCase;
import henrique.igor.iiot.application.usecases.workOrder.CreateWorkOrderUseCase;
import henrique.igor.iiot.application.usecases.workOrder.GetWorkOrderSheetUseCase;
import henrique.igor.iiot.application.usecases.workOrder.UpdateWorkOrderStatusUseCase;
import henrique.igor.iiot.application.usecases.workOrder.dto.WorkOrderSheetOutput;
import henrique.igor.iiot.domain.entities.WorkOrder;
import henrique.igor.iiot.infrastructure.web.dtos.workOrder.AssignWorkOrderRequest;
import henrique.igor.iiot.infrastructure.web.dtos.workOrder.CloseWorkOrderRequest;
import henrique.igor.iiot.infrastructure.web.dtos.workOrder.CreateWorkOrderRequest;
import henrique.igor.iiot.infrastructure.web.dtos.workOrder.UpdateWorkOrderStatusRequest;
import henrique.igor.iiot.infrastructure.web.dtos.workOrder.WorkOrderResponse;
import henrique.igor.iiot.infrastructure.web.specs.WorkOrderControllerSpec;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/work-orders")
public class WorkOrderController implements WorkOrderControllerSpec {

    private final CreateWorkOrderUseCase createWorkOrderUseCase;
    private final AssignWorkOrderUseCase assignWorkOrderUseCase;
    private final UpdateWorkOrderStatusUseCase updateWorkOrderStatusUseCase;
    private final CloseWorkOrderUseCase closeWorkOrderUseCase;
    private final GetWorkOrderSheetUseCase getWorkOrderSheetUseCase;

    public WorkOrderController(
            CreateWorkOrderUseCase createWorkOrderUseCase,
            AssignWorkOrderUseCase assignWorkOrderUseCase,
            UpdateWorkOrderStatusUseCase updateWorkOrderStatusUseCase,
            CloseWorkOrderUseCase closeWorkOrderUseCase,
            GetWorkOrderSheetUseCase getWorkOrderSheetUseCase
    ) {
        this.createWorkOrderUseCase = createWorkOrderUseCase;
        this.assignWorkOrderUseCase = assignWorkOrderUseCase;
        this.updateWorkOrderStatusUseCase = updateWorkOrderStatusUseCase;
        this.closeWorkOrderUseCase = closeWorkOrderUseCase;
        this.getWorkOrderSheetUseCase = getWorkOrderSheetUseCase;
    }

    @Override
    @PostMapping
    public ResponseEntity<WorkOrderResponse> create(@Valid @RequestBody CreateWorkOrderRequest request) {
        WorkOrder createdWorkOrder = createWorkOrderUseCase.execute(request.toInput());
        return ResponseEntity.status(HttpStatus.CREATED).body(WorkOrderResponse.fromDomain(createdWorkOrder));
    }

    @Override
    @PatchMapping("/{id}/assign")
    public ResponseEntity<WorkOrderResponse> assignTechnician(
            @PathVariable UUID id,
            @Valid @RequestBody AssignWorkOrderRequest request
    ) {
        WorkOrder updatedWorkOrder = assignWorkOrderUseCase.execute(request.toInput(id));
        return ResponseEntity.ok(WorkOrderResponse.fromDomain(updatedWorkOrder));
    }

    @Override
    @PatchMapping("/{id}/status")
    public ResponseEntity<WorkOrderResponse> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateWorkOrderStatusRequest request
    ) {
        WorkOrder updatedWorkOrder = updateWorkOrderStatusUseCase.execute(request.toInput(id));
        return ResponseEntity.ok(WorkOrderResponse.fromDomain(updatedWorkOrder));
    }

    @Override
    @PatchMapping("/{id}/close")
    public ResponseEntity<WorkOrderResponse> close(
            @PathVariable UUID id,
            @Valid @RequestBody CloseWorkOrderRequest request
    ) {
        WorkOrder closedWorkOrder = closeWorkOrderUseCase.execute(request.toInput(id));
        return ResponseEntity.ok(WorkOrderResponse.fromDomain(closedWorkOrder));
    }

    @Override
    @GetMapping("/{id}/sheet")
    public ResponseEntity<WorkOrderSheetOutput> getSheet(@PathVariable UUID id) {
        WorkOrderSheetOutput sheet = getWorkOrderSheetUseCase.execute(id);
        return ResponseEntity.ok(sheet);
    }
}
