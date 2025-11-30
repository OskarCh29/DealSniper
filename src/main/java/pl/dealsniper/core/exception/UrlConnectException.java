/* (C) 2025 */
package pl.dealsniper.core.exception;

import lombok.Getter;

@Getter
public class UrlConnectException extends RuntimeException {
    private final UriValidationError reason;

    public UrlConnectException(String message, UriValidationError reason) {
        super(message);
        this.reason = reason;
    }
}
