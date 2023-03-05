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
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionApiHandler {
    private static final Logger log = LogManager.getLogger(ExceptionApiHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationArgumentException(MethodArgumentNotValidException e) {
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
                                .collect(Collectors.toList()).stream().findFirst().get()
                );
    }

    @ExceptionHandler(value = {StartTimeEventException.class, IllegalStateEventException.class})
    public ResponseEntity<ErrorResponse> handleStartTimeEventException2(Exception e) {
        log.error(e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseBuilder()
                        .setStatus(HttpStatus.CONFLICT.name())
                        .setReason("For the requested operation the conditions are not met.")
                        .setMessage(e.getMessage())
                        .createErrorResponse()
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

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(
            NoSuchElementException e) {
        log.error(e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseBuilder()
                        .setStatus(HttpStatus.NOT_FOUND.name())
                        .setReason("The required object was not found.")
                        .setMessage(e.getLocalizedMessage())
                        .createErrorResponse()
                );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException e) {
        log.error(e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseBuilder()
                        .setStatus(HttpStatus.BAD_REQUEST.name())
                        .setReason("Значение параметра не отвечает требованиям")
                        .setMessage(e.getLocalizedMessage())
                        .createErrorResponse()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception e) {
        log.error(e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseBuilder()
                        .setStatus(HttpStatus.INTERNAL_SERVER_ERROR.name())
                        .setReason("unhandled exception")
                        .setMessage(e.getLocalizedMessage())
                        .createErrorResponse()
                );
    }
}
