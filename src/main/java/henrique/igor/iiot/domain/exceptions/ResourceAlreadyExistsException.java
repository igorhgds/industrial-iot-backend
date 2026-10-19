package henrique.igor.iiot.domain.exceptions;

public class ResourceAlreadyExistsException extends BusinessException {

    private final String resource;
    private final String field;
    private final Object value;

    public ResourceAlreadyExistsException(String resource, String field, Object value) {
        super(String.format("%s already exists with %s: '%s'", resource, field, value));
        this.resource = resource;
        this.field = field;
        this.value = value;
    }

    public ResourceAlreadyExistsException(String resource, Object identifier) {
        this(resource, "identifier", identifier);
    }

    public String getResource() {
        return resource;
    }

    public String getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }
}