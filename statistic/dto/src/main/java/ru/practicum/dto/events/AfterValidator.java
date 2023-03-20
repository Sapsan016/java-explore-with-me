package ru.practicum.dto.events;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class AfterValidator implements ConstraintValidator<After, LocalDateTime> {

    private final LocalDateTime date = LocalDateTime.now();

//    public void initialize(After annotation) {
//        date = LocalDateTime.now();
//    }

    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        boolean valid = true;
        if (value != null) {
            if (!value.isAfter(date.plusHours(2))) {
                valid = false;
            }
        }
        return valid;
    }
}
