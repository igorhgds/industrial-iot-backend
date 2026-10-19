package henrique.igor.iiot.application.usecases.user;

import henrique.igor.iiot.application.usecases.user.dto.CreateUserInput;
import henrique.igor.iiot.domain.entities.Sector;
import henrique.igor.iiot.domain.entities.User;
import henrique.igor.iiot.domain.exceptions.ResourceAlreadyExistsException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.SectorRepository;
import henrique.igor.iiot.domain.repositories.UserRepository;

public class CreateUserUseCase {

    private final UserRepository userRepository;
    private final SectorRepository sectorRepository;

    public CreateUserUseCase(UserRepository userRepository, SectorRepository sectorRepository) {
        this.userRepository = userRepository;
        this.sectorRepository = sectorRepository;
    }

    public User execute(CreateUserInput input){
        if (userRepository.findByEmail(input.email()).isPresent()) {
            throw new ResourceAlreadyExistsException("User", "email", input.email());
        }

        Sector sector = null;
        if (input.sectorId() != null) {
            sector = sectorRepository.findById(input.sectorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sector", input.sectorId()));
        }

        User newUser = new User(
                input.name(),
                input.email(),
                input.password(),
                input.passwordRecovery(),
                input.userRole(),
                sector
        );

        return userRepository.save(newUser);
    }
}
