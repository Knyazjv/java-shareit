package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(final Exception e) {
        log.warn("Error: ", e);
        return new ErrorResponse("Error: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        log.warn("Email is used:", e);
        return new ErrorResponse("Error: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponseSimple handleBookingStateException(final BookingStateException e) {
        log.warn("Error: ", e);
        return new ErrorResponseSimple(e.getMessage());
    }
}