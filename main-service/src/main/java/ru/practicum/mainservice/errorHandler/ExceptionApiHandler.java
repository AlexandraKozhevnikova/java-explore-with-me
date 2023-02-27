package ru.practicum.mainservice.errorHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionApiHandler {
    private static final Logger log = LogManager.getLogger(ExceptionApiHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorResponse>> handleValidationException(MethodArgumentNotValidException e) {

        List<FieldError> listError = e.getBindingResult().getFieldErrors();
        log.error(e.getMessage(), e);

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                listError.stream()
                    .map(it ->
                        new ErrorResponseBuilder()
                            .setStatus("BAD_REQUEST")
                            .setReason("Incorrectly made request.")
                            .setMessage("Field: " + it.getField() +
                                ". Error: " + it.getDefaultMessage() +
                                ". Value: " + it.getRejectedValue())
                            .createErrorResponse()

                    )
                    .collect(Collectors.toList())
            );
    }
}
