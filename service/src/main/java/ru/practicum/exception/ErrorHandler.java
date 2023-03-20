package ru.practicum.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
public class ErrorHandler {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse objectNotFoundExceptionResponse(final ObjectNotFoundException e) {
        return new ErrorResponse(e.getMessage(),
                "NOT_FOUND", "The required object was not found.", LocalDateTime.now().format(FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse badRequest(final MethodArgumentNotValidException e) {
        return new ErrorResponse(e.getMessage(),
                "BAD_REQUEST", "Incorrectly made request.", LocalDateTime.now().format(FORMATTER));
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse badArgumentRequest(final MethodArgumentTypeMismatchException e) {
        return new ErrorResponse(e.getMessage(),
                "BAD_REQUEST", "Incorrectly made request.", LocalDateTime.now().format(FORMATTER));
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse dataViolationException(final DataIntegrityViolationException e) {
        return new ErrorResponse(e.getMessage(),
                "CONFLICT", "Integrity constraint has been violated.",
                LocalDateTime.now().format(FORMATTER));
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse eventStateViolationException(final IllegalArgumentException e) {
        return new ErrorResponse(e.getMessage(),
                "BAD_REQUEST", "For the requested operation the conditions are not met.",
                LocalDateTime.now().format(FORMATTER));
    }

}
