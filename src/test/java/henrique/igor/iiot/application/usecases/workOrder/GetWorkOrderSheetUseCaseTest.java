package henrique.igor.iiot.application.usecases.workOrder;

import henrique.igor.iiot.application.usecases.workOrder.dto.WorkOrderSheetOutput;
import henrique.igor.iiot.domain.entities.*;
import henrique.igor.iiot.domain.entities.enums.*;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.WorkOrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetWorkOrderSheetUseCaseTest {

    @Mock
    private WorkOrderRepository workOrderRepository;

    @InjectMocks
    private GetWorkOrderSheetUseCase getWorkOrderSheetUseCase;

    @Test
    @DisplayName("Should build complete printable work order sheet DTO with equipment, technician, alert and snapshots")
    void shouldBuildCompleteWorkOrderSheetSuccessfully() {
        UUID workOrderId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        Sector sector = new Sector(UUID.randomUUID(), "Usinagem", "Setor 1", now);
        Gateway gateway = new Gateway(UUID.randomUUID(), "GW-USI-001", "MAC", "10.0.0.1", "v1", GatewayStatus.ONLINE, sector, now, now);
        Equipment equipment = new Equipment(UUID.randomUUID(), "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, sector, gateway, now);
        RuleGroup ruleGroup = new RuleGroup(UUID.randomUUID(), "Overheating Rule", "Desc", equipment, Severity.CRITICAL, LogicalOperator.AND, true, now);
        Sensor tempSensor = new Sensor(UUID.randomUUID(), "TEM-001-MTR-USI-001", SensorType.TEMPERATURE, SensorStatus.ONLINE, "°C", "topic", equipment, now);
        AlertSensorSnapshot snapshot = new AlertSensorSnapshot(tempSensor, new BigDecimal("98.5"));
        Alert alert = new Alert(UUID.randomUUID(), equipment, ruleGroup, Severity.CRITICAL, "Critical overheat detected", AlertStatus.ACKNOWLEDGED, null, now, null, List.of(snapshot));
        User technician = new User(UUID.randomUUID(), "Carlos Silva", "carlos@iiot.com", "hash", "rec", UserRole.TECHNICIAN, sector, now);

        WorkOrder workOrder = new WorkOrder(
                workOrderId,
                alert,
                equipment,
                technician,
                "Emergency Motor Overheat Repair",
                "Inspect cooling fan and bearings",
                WorkOrderPriority.CRITICAL,
                WorkOrderStatus.IN_PROGRESS,
                now,
                null
        );

        when(workOrderRepository.findById(workOrderId)).thenReturn(Optional.of(workOrder));

        WorkOrderSheetOutput sheet = getWorkOrderSheetUseCase.execute(workOrderId);

        assertNotNull(sheet);
        assertEquals(workOrderId, sheet.workOrderId());
        assertEquals("Emergency Motor Overheat Repair", sheet.title());
        assertEquals(WorkOrderPriority.CRITICAL, sheet.priority());
        assertEquals(WorkOrderStatus.IN_PROGRESS, sheet.status());
        assertEquals("MTR-USI-001", sheet.equipmentCode());
        assertEquals(EquipType.MOTOR, sheet.equipmentType());
        assertEquals("Usinagem", sheet.sectorName());
        assertEquals("GW-USI-001", sheet.gatewayCode());
        assertEquals("Carlos Silva", sheet.technicianName());
        assertEquals("carlos@iiot.com", sheet.technicianEmail());
        assertEquals("Overheating Rule", sheet.ruleGroupName());
        assertEquals(Severity.CRITICAL, sheet.alertSeverity());
        assertEquals(1, sheet.sensorSnapshots().size());
        assertEquals("TEM-001-MTR-USI-001", sheet.sensorSnapshots().get(0).sensorCode());
        assertEquals("°C", sheet.sensorSnapshots().get(0).unit());
        assertEquals(new BigDecimal("98.5"), sheet.sensorSnapshots().get(0).valueAtTrigger());
    }

    @Test
    @DisplayName("Should throw exception when workOrderId is null")
    void shouldThrowExceptionWhenIdIsNull() {
        assertThrows(DomainValidationException.class, () -> getWorkOrderSheetUseCase.execute(null));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when work order does not exist")
    void shouldThrowExceptionWhenWorkOrderNotFound() {
        UUID workOrderId = UUID.randomUUID();
        when(workOrderRepository.findById(workOrderId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> getWorkOrderSheetUseCase.execute(workOrderId));
    }
}
