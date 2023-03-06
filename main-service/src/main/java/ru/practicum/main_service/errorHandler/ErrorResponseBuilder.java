package ru.practicum.main_service.errorHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class ErrorResponseBuilder {
    private String timestamp;
    private String status;
    private String message;
    private String reason;

    public ErrorResponseBuilder setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public ErrorResponseBuilder setStatus(String status) {
        this.status = status;
        return this;
    }

    public ErrorResponseBuilder setMessage(String message) {
        this.message = message;
        return this;
    }

    public ErrorResponseBuilder setReason(String reason) {
        this.reason = reason;
        return this;
    }

    public ErrorResponse createErrorResponse() {
        return new ErrorResponse(
                Objects.requireNonNullElseGet(
                        timestamp, () -> LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                ),
                status, message, reason);
    }
}