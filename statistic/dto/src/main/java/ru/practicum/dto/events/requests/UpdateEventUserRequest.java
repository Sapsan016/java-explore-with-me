package ru.practicum.dto.events.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.events.validators.After;
import ru.practicum.dto.events.states.UserEventActionState;
import ru.practicum.model.Location;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventUserRequest {

    @Size(min = 20, message = "Field: annotation. Error: must not be less than 20 characters.")
    @Size(max = 2000, message = "Field: annotation. Error: must not be more than 2000 characters.")
    String annotation;

    Long category;

    @Size(min = 20, message = "Field: annotation. Error: must not be less than 20 characters.")
    @Size(max = 7000, message = "Field: annotation. Error: must not be more than 7000 characters.")
    String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @After(message = "Field: eventDate. Error: должно содержать дату, которая еще не наступила.")
    LocalDateTime eventDate;

    Location location;

    Boolean paid;

    Integer participantLimit;

    Boolean requestModeration;

    UserEventActionState stateAction;

    @Size(max = 120, message = "Field: annotation. Error: must not be more than 120 characters.")
    @Size(min = 3, message = "Field: annotation. Error: must not be less than 3 characters.")
    String title;

}
