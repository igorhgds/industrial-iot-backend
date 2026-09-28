package henrique.igor.iiot.application.usecases.user;

import henrique.igor.iiot.application.usecases.user.dto.UpdateUserRequest;
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

    public User execute(UpdateUserRequest request) {
        User existingUser = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + request.userId()));

        if (!existingUser.getEmail().equalsIgnoreCase(request.email())) {
            userRepository.findByEmail(request.email()).ifPresent(u -> {
                throw new IllegalArgumentException("User with email " + request.email() + " already exists.");
            });
        }

        Sector sector = null;
        if (request.sectorId() != null) {
            sector = sectorRepository.findById(request.sectorId())
                    .orElseThrow(() -> new IllegalArgumentException("Sector not found with ID: " + request.sectorId()));
        }

        existingUser.updateProfile(request.name(), request.email());
        existingUser.changeRole(request.userRole());
        existingUser.relocateToSector(sector);

        return userRepository.save(existingUser);
    }
}
