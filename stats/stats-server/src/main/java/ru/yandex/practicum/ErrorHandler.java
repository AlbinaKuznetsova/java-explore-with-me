package ru.yandex.practicum;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(IllegalArgumentException e) {
        return new ApiError(e.getMessage());
    }

    @Getter
    class ApiError {
        private final String message;

        public ApiError(String message) {
            this.message = message;
        }
    }
}
