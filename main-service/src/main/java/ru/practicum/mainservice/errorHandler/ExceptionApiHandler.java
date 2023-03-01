package ru.practicum.mainservice.errorHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionApiHandler {
    private static final Logger log = LogManager.getLogger(ExceptionApiHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorResponse>> handleValidationArgumentException(MethodArgumentNotValidException e) {
        List<FieldError> listError = e.getBindingResult().getFieldErrors();
        log.error(e.getMessage(), e);

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                listError.stream()
                    .map(it ->
                        new ErrorResponseBuilder()
                            .setStatus(HttpStatus.BAD_REQUEST.name())
                            .setReason("Incorrectly made request.")
                            .setMessage("Field: " + it.getField() +
                                ". Error: " + it.getDefaultMessage() +
                                ". Value: " + it.getRejectedValue())
                            .createErrorResponse()

                    )
                    .collect(Collectors.toList())
            );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
        DataIntegrityViolationException e) {
        log.error(e.getMessage(), e);

        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(new ErrorResponseBuilder()
                .setStatus(HttpStatus.CONFLICT.name())
                .setReason("Integrity constraint has been violated.")
                .setMessage(e.getLocalizedMessage())
                .createErrorResponse()
            );
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> handleValidationRequestException(
        Exception e) {
        log.error(e.getMessage(), e);

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponseBuilder()
                .setStatus(HttpStatus.BAD_REQUEST.name())
                .setReason("Incorrectly made request.")
                .setMessage(e.getLocalizedMessage())
                .createErrorResponse()
            );
    }
}
