package ru.practicum.shareit.exception;

public class EmptyItemException extends RuntimeException {
        public EmptyItemException(String message) {
            super(message);
        }
}