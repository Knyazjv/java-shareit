package ru.practicum.shareit.booking.annotation;



import ru.practicum.shareit.booking.dto.BookingDtoRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEnd, BookingDtoRequest> {

    @Override
    public void initialize(StartBeforeEnd constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(BookingDtoRequest bookingDtoRequest, ConstraintValidatorContext context) {
        if (bookingDtoRequest.getStart() == null || bookingDtoRequest.getEnd() == null) {
            return false;
        }
        return bookingDtoRequest.getStart().isBefore(bookingDtoRequest.getEnd());
    }
}
