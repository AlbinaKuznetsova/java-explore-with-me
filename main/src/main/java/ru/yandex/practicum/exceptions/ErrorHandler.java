package ru.yandex.practicum.exceptions;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
public class ErrorHandler {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(ObjectNotFoundException e) {
        return new ApiError(e.getMessage(), e.getCause().toString(), HttpStatus.NOT_FOUND.toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConstraintViolationException(ConstraintViolationException e) {
        return new ApiError(e.getMessage(), e.getCause().toString(), HttpStatus.CONFLICT.toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleForbiddenOperationException(ForbiddenOperationException e) {
        return new ApiError(e.getMessage(), e.getCause().toString(), HttpStatus.CONFLICT.toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleNumberFormatException(NumberFormatException e) {
        return new ApiError(e.getMessage(), e.getCause().toString(), HttpStatus.BAD_REQUEST.toString());
    }

    class ApiError {
        private final String message;
        private final String reason;
        private final String status;
        private final String timestamp = LocalDateTime.now().format(formatter);

        public ApiError(String message, String reason, String status) {
            this.message = message;
            this.reason = reason;
            this.status = status;
        }


        public String getMessage() {
            return message;
        }

        public String getReason() {
            return reason;
        }

        public String getStatus() {
            return status;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }
}
