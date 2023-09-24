package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleNotFoundExceptionTest() {
        NotFoundException exception = new NotFoundException("Message");
        ErrorResponse errorResponse = errorHandler.handleNotFoundException(exception);

        assertNotNull(errorResponse);
        assertEquals(errorResponse.getDescription(), exception.getMessage());
    }

    @Test
    void handleExceptionTest() {
        Exception exception = new Exception("Message");
        ErrorResponse errorResponse = errorHandler.handleException(exception);

        assertNotNull(errorResponse);
        assertEquals(errorResponse.getDescription(), exception.getMessage());
    }

    @Test
    void handleCommentExceptionTest() {
        CommentException exception = new CommentException("Message");
        ErrorResponse errorResponse = errorHandler.handleCommentException(exception);

        assertNotNull(errorResponse);
        assertEquals(errorResponse.getDescription(), exception.getMessage());
    }

    @Test
    void handleDataIntegrityViolationExceptionTest() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Message");
        ErrorResponse errorResponse = errorHandler.handleDataIntegrityViolationException(exception);

        assertNotNull(errorResponse);
        assertEquals(errorResponse.getDescription(), exception.getMessage());
    }

    @Test
    void handleBookingStateExceptionTest() {
        BookingStateException exception = new BookingStateException("Message");
        ErrorResponseSimple errorResponse = errorHandler.handleBookingStateException(exception);

        assertNotNull(errorResponse);
        assertEquals(errorResponse.getError(), exception.getMessage());
    }

    @Test
    void handleBookingExceptionTest() {
        BookingException exception = new BookingException("Message");
        ErrorResponse errorResponse = errorHandler.handleBookingException(exception);

        assertNotNull(errorResponse);
        assertEquals(errorResponse.getDescription(), exception.getMessage());
    }


    @Test
    void handleEmptyItemExceptionTest() {
        EmptyException  exception = new EmptyException("Message");
        ErrorResponse errorResponse = errorHandler.handleEmptyItemException(exception);

        assertNotNull(errorResponse);
        assertEquals(errorResponse.getDescription(), exception.getMessage());
    }

    @Test
    void handlePaginationExceptionTest() {
        PaginationException exception = new PaginationException("Message");
        ErrorResponse errorResponse = errorHandler.handlePaginationException(exception);

        assertNotNull(errorResponse);
        assertEquals(errorResponse.getDescription(), exception.getMessage());
    }
}