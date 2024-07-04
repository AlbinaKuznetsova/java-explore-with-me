package ru.yandex.practicum.exceptions;

public class ForbiddenOperationException extends RuntimeException {
    public ForbiddenOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
