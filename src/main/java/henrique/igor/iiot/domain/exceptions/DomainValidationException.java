package henrique.igor.iiot.domain.exceptions;

public class DomainValidationException extends BusinessException {
    public DomainValidationException(String message) {
        super(message);
    }
}
