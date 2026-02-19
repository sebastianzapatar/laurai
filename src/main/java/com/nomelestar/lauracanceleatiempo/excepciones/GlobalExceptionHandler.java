package com.nomelestar.lauracanceleatiempo.excepciones;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(NotFoundException ex,
                                                            HttpServletRequest re) {
        ApiError apiError = new ApiError();
        apiError.setTimestamp(Instant.now());
        apiError.setStatus(HttpStatus.NOT_FOUND.value());
        apiError.setError(ex.getMessage());
        apiError.setPath(re.getRequestURI());
        return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, HttpServletRequest req
    ){
        ApiError apiError = new ApiError();
        Map<String, String> errors = new HashMap<>();
        for(FieldError fieldError : ex.getBindingResult().getFieldErrors()){
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        apiError.setTimestamp(Instant.now());
        apiError.setStatus(HttpStatus.BAD_REQUEST.value());
        apiError.setFieldsErrors(errors);
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);

    }
}
