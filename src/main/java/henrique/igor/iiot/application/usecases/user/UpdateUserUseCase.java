package henrique.igor.iiot.application.usecases.user;

import henrique.igor.iiot.application.usecases.user.dto.UpdateUserInput;
import henrique.igor.iiot.domain.entities.Sector;
import henrique.igor.iiot.domain.entities.User;
import henrique.igor.iiot.domain.repositories.SectorRepository;
import henrique.igor.iiot.domain.repositories.UserRepository;

public class UpdateUserUseCase {

    private final UserRepository userRepository;
    private final SectorRepository sectorRepository;

    public UpdateUserUseCase(UserRepository userRepository, SectorRepository sectorRepository) {
        this.userRepository = userRepository;
        this.sectorRepository = sectorRepository;
    }

    public User execute(UpdateUserInput input) {
        User existingUser = userRepository.findById(input.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + input.userId()));

        if (!existingUser.getEmail().equalsIgnoreCase(input.email())) {
            userRepository.findByEmail(input.email()).ifPresent(u -> {
                throw new IllegalArgumentException("User with email " + input.email() + " already exists.");
            });
        }

        Sector sector = null;
        if (input.sectorId() != null) {
            sector = sectorRepository.findById(input.sectorId())
                    .orElseThrow(() -> new IllegalArgumentException("Sector not found with ID: " + input.sectorId()));
        }

        existingUser.updateProfile(input.name(), input.email());
        existingUser.changeRole(input.userRole());
        existingUser.relocateToSector(sector);

        return userRepository.save(existingUser);
    }
}
