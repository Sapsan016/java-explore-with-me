package ru.practicum.dto.events.validators;

public class TimeValidationException extends RuntimeException{

    public TimeValidationException() {
    }

    public TimeValidationException(String message) {
        super(message);
    }

}

