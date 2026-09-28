package henrique.igor.iiot.application.usecases.user;

import henrique.igor.iiot.domain.entities.User;
import henrique.igor.iiot.domain.repositories.UserRepository;

import java.util.UUID;

public class GetUserByIdUseCase {

    private final UserRepository userRepository;

    public GetUserByIdUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(UUID userId){
        if (userId == null){
            throw new IllegalArgumentException("User ID cannot be null.");
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
    }
}
