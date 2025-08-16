package pl.dealsniper.core.exception;

public class InsertFailedException extends RuntimeException {

    private final String entityName;
    private final Object entityId;

    public InsertFailedException(String entityName, Object entityId) {
        super(String.format("Insert failed for entity %s with id %s", entityName, entityId));
        this.entityName = entityName;
        this.entityId = entityId;
    }

    public InsertFailedException(String entityName, Object entityId, String additionalMessage) {
        super(String.format("Insert failed for entity %s with id %s: %s", entityName, entityId, additionalMessage));
        this.entityName = entityName;
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public Object getEntityId() {
        return entityId;
    }
}
