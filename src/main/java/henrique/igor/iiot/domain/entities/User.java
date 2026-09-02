package henrique.igor.iiot.domain.entities;

import henrique.igor.iiot.domain.entities.enums.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {

    private final UUID userId;
    private String name;
    private String email;
    private String password;
    private String passwordRecovery;
    private UserRole userRole;
    private Sector sector;
    private final LocalDateTime createdAt;

    public User(String name, String email, String password, String passwordRecovery,UserRole userRole, Sector sector){
        this(UUID.randomUUID(), name, email, password, passwordRecovery ,userRole, sector, LocalDateTime.now());
    }

    public User(UUID userId, String name, String email, String password, String passwordRecovery, UserRole userRole, Sector sector, LocalDateTime createdAt) {
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
    public LocalDateTime getCreatedAt() {return createdAt;}
}
