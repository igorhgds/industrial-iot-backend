package henrique.igor.iiot.application.usecases.user;

import henrique.igor.iiot.domain.entities.User;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.UserRepository;

import java.util.UUID;

public class GetUserByIdUseCase {

    private final UserRepository userRepository;

    public GetUserByIdUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(UUID userId){
        if (userId == null){
            throw new DomainValidationException("User ID cannot be null.");
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }
}
