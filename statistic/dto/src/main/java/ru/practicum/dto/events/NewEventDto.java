package ru.practicum.dto.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.events.validators.After;
import ru.practicum.model.Location;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {


    @NotBlank(message = "Field: annotation. Error: must not be blank. Value: null")
    @Size(min = 20, message = "Field: annotation. Error: must not be less than 20 characters.")
    @Size(max = 2000, message = "Field: annotation. Error: must not be more than 2000 characters.")
    String annotation;

    Long category;

    @Size(min = 20, message = "Field: annotation. Error: must not be less than 20 characters.")
    @Size(max = 7000, message = "Field: annotation. Error: must not be more than 7000 characters.")
    String description;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @After(message = "Field: eventDate. Error: должно содержать дату, которая еще не наступила.")
    LocalDateTime eventDate;

    @NotNull
    Location location;

    Boolean paid;

    Integer participantLimit;

    Boolean requestModeration;

    @NotBlank(message = "Field: annotation. Error: must not be blank. Value: null")
    @Size(max = 120, message = "Field: annotation. Error: must not be more than 120 characters.")
    @Size(min = 3, message = "Field: annotation. Error: must not be less than 3 characters.")
    String title;

}

