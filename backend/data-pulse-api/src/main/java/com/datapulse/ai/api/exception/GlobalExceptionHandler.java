package com.datapulse.ai.api.exception;

import com.datapulse.ai.shared.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getDescription(false));
    }
    
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> handleRuntime(RuntimeException ex, WebRequest request) {
        log.error("Runtime exception during request handling", ex);
        if (ex.getMessage() != null && ex.getMessage().contains("not found")) {
            return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request.getDescription(false));
        }
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred", request.getDescription(false));
    }

    private ResponseEntity<ApiErrorResponse> buildError(HttpStatus status, String error, String path) {
        String cleanPath = path.replace("uri=", "");
        String traceId = UUID.randomUUID().toString(); // In a real app, grab from brave/sleuth/micrometer tracer
        
        ApiErrorResponse response = new ApiErrorResponse(
            Instant.now(),
            status.value(),
            error,
            cleanPath,
            traceId
        );
        return ResponseEntity.status(status).body(response);
    }
}
