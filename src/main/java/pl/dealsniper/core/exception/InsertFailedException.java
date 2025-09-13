/* (C) 2025 */
package pl.dealsniper.core.exception;

import lombok.Getter;

@Getter
public class InsertFailedException extends RuntimeException {

    private final String entityName;
    private final Object entityId;

    public InsertFailedException(String entityName, Object entityId) {
        super(String.format("Insert failed for entity %s with id %s", entityName, entityId));
        this.entityName = entityName;
        this.entityId = entityId;
    }
}
