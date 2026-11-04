package henrique.igor.iiot.domain.exceptions;

public class GatewayMismatchException extends ResourceOwnershipMismatchException {
    public GatewayMismatchException(String message) {
        super(message);
    }
}
