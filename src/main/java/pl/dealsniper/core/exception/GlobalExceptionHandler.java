/* (C) 2025 */
package pl.dealsniper.core.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.dealsniper.core.dto.response.ErrorResponse;
import pl.dealsniper.core.exception.db.InsertFailedException;
import pl.dealsniper.core.exception.db.RecordNotFoundException;
import pl.dealsniper.core.exception.db.ResourceUsedException;
import pl.dealsniper.core.exception.scheduler.ScheduledTaskException;
import pl.dealsniper.core.exception.url.UrlConnectException;
import pl.dealsniper.core.exception.user.UserInactiveException;
import pl.dealsniper.core.exception.verification.VerificationCodeException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Consumer<String> WARN_LOG = log::warn;
    private static final Consumer<String> ERROR_LOG = log::error;

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalStateException(IllegalStateException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), request, WARN_LOG);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), request, WARN_LOG);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(","));
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request, WARN_LOG);
    }

    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRecordNotFound(RecordNotFoundException ex, HttpServletRequest request) {

        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request, WARN_LOG);
    }

    @ExceptionHandler(InsertFailedException.class)
    public ResponseEntity<ErrorResponse> handleInsertFailedException(
            InsertFailedException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request, WARN_LOG);
    }

    @ExceptionHandler(ResourceUsedException.class)
    public ResponseEntity<ErrorResponse> handleResourceUsedException(
            ResourceUsedException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request, WARN_LOG);
    }

    @ExceptionHandler(UserInactiveException.class)
    public ResponseEntity<ErrorResponse> handleUserInactiveException(
            UserInactiveException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request, WARN_LOG);
    }

    @ExceptionHandler(VerificationCodeException.class)
    public ResponseEntity<ErrorResponse> handleVerificationCodeException(
            VerificationCodeException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request, WARN_LOG);
    }

    @ExceptionHandler(ScheduledTaskException.class)
    public ResponseEntity<ErrorResponse> handleScheduledTaskException(
            ScheduledTaskException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request, WARN_LOG);
    }

    @ExceptionHandler(UrlConnectException.class)
    public ResponseEntity<ErrorResponse> handleUriConnectException(UrlConnectException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request, WARN_LOG);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameterException(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request, WARN_LOG);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            HttpStatus status, String message, HttpServletRequest request, Consumer<String> logger) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(status.value())
                .code(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURL().toString())
                .timeStamp(LocalDateTime.now())
                .build();

        logger.accept(message);

        return ResponseEntity.status(status).body(errorResponse);
    }
}
