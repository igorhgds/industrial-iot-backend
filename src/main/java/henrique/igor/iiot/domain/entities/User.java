package henrique.igor.iiot.domain.entities;

import henrique.igor.iiot.domain.entities.enums.UserRole;

import java.time.OffsetDateTime;
import java.util.UUID;

public class User {

    private final UUID userId;
    private String name;
    private String email;
    private String password;
    private String passwordRecovery;
    private UserRole userRole;
    private Sector sector;
    private final OffsetDateTime createdAt;

    public User(String name, String email, String password, String passwordRecovery,UserRole userRole, Sector sector){
        this(UUID.randomUUID(), name, email, password, passwordRecovery ,userRole, sector, OffsetDateTime.now());
    }

    public User(UUID userId, String name, String email, String password, String passwordRecovery, UserRole userRole, Sector sector, OffsetDateTime createdAt) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.passwordRecovery = passwordRecovery;
        this.userRole = userRole;
        this.sector = sector;
        this.createdAt = createdAt;
    }

    public UUID getUserId() {return userId;}
    public String getName() {return name;}
    public String getEmail() {return email;}
    public String getPassword() {return password;}
    public String getPasswordRecovery() {return passwordRecovery;}
    public UserRole getUserRole() {return userRole;}
    public Sector getSector() {return sector;}
    public OffsetDateTime getCreatedAt() {return createdAt;}
}
