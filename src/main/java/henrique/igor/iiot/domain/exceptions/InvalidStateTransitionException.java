package henrique.igor.iiot.domain.exceptions;

public class InvalidStateTransitionException extends BusinessException {
    public InvalidStateTransitionException(String message) {
        super(message);
    }
}
