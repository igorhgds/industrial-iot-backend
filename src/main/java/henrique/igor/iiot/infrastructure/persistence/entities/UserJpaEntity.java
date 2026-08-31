package henrique.igor.iiot.infrastructure.persistence.entities;

import henrique.igor.iiot.domain.entities.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
public class UserJpaEntity {

    @Id
    private UUID userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String passwordRecovery;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_id")
    private SectorJpaEntity sector;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public UserJpaEntity(){}

    public UserJpaEntity(UUID userId, String name, String email, String password, String passwordRecovery, UserRole userRole, SectorJpaEntity sector, LocalDateTime createdAt) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.passwordRecovery = passwordRecovery;
        this.userRole = userRole;
        this.sector = sector;
        this.createdAt = createdAt;
    }
}
