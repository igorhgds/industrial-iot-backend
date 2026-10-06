package henrique.igor.iiot.infrastructure.persistence.entities;

import henrique.igor.iiot.domain.entities.enums.HealthStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.JdbcTypeCode;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "equipment_state")
@Getter
@Setter
public class EquipmentStateJpaEntity {

    @Id
    private UUID equipmentId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "equipment_id")
    private EquipmentJpaEntity equipment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HealthStatus status;

    @JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @ColumnTransformer(write = "?::jsonb")
    @Column(name = "last_payload", columnDefinition = "jsonb")
    private String lastPayload;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    public EquipmentStateJpaEntity(){}

    public EquipmentStateJpaEntity(EquipmentJpaEntity equipment, HealthStatus status, String lastPayload, OffsetDateTime updatedAt) {
        this.equipment = equipment;
        this.status = status;
        this.lastPayload = lastPayload;
        this.updatedAt = updatedAt;
    }
}
