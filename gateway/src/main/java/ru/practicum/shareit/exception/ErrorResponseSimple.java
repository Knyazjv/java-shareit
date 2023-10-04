package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class ErrorResponseSimple {
    private final String error;

    public ErrorResponseSimple(String error) {
        this.error = error;
    }
}
