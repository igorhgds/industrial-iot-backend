package henrique.igor.iiot.infrastructure.web.exception;

import henrique.igor.iiot.domain.exceptions.ActuatorOperationException;
import henrique.igor.iiot.domain.exceptions.BusinessException;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.GatewayMismatchException;
import henrique.igor.iiot.domain.exceptions.InvalidRoleAssignmentException;
import henrique.igor.iiot.domain.exceptions.InvalidStateTransitionException;
import henrique.igor.iiot.domain.exceptions.ResourceAlreadyExistsException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.exceptions.ResourceOwnershipMismatchException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler({ResourceAlreadyExistsException.class, InvalidStateTransitionException.class})
    public ResponseEntity<ApiErrorResponse> handleConflict(
            BusinessException ex,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler({
            ResourceOwnershipMismatchException.class,
            GatewayMismatchException.class,
            InvalidRoleAssignmentException.class,
            ActuatorOperationException.class
    })
    public ResponseEntity<ApiErrorResponse> handleUnprocessableContent(
            BusinessException ex,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler({DomainValidationException.class, BusinessException.class})
    public ResponseEntity<ApiErrorResponse> handleDomainValidation(
            BusinessException ex,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return buildResponse(HttpStatus.BAD_REQUEST, details, request.getRequestURI());
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String message, String path) {
        ApiErrorResponse body = new ApiErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                OffsetDateTime.now()
        );
        return ResponseEntity.status(status).body(body);
    }
}
