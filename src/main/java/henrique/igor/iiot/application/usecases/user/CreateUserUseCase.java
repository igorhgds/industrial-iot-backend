package henrique.igor.iiot.application.usecases.user;

import henrique.igor.iiot.application.usecases.user.dto.CreateUserRequest;
import henrique.igor.iiot.domain.entities.Sector;
import henrique.igor.iiot.domain.entities.User;
import henrique.igor.iiot.domain.repositories.SectorRepository;
import henrique.igor.iiot.domain.repositories.UserRepository;

public class CreateUserUseCase {

    private final UserRepository userRepository;
    private final SectorRepository sectorRepository;

    public CreateUserUseCase(UserRepository userRepository, SectorRepository sectorRepository) {
        this.userRepository = userRepository;
        this.sectorRepository = sectorRepository;
    }

    public User execute(CreateUserRequest request){
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("User with email " + request.email() + "already exists.");
        }

        Sector sector = null;
        if (request.sectorId() != null) {
            sector = sectorRepository.findById(request.sectorId())
                    .orElseThrow(() -> new IllegalArgumentException("Sector not found with ID: " + request.sectorId()));
        }

        User newUser = new User(
                request.name(),
                request.email(),
                request.password(),
                request.passwordRecovery(),
                request.userRole(),
                sector
        );

        return userRepository.save(newUser);
    }
}
