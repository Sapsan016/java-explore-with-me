package ru.practicum.dto.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.Location;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {

    static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.now();

    @NotBlank(message = "Field: annotation. Error: must not be blank. Value: null")
    @Max(value = 2000, message = "Field: annotation. Error: must not be more than 2000 characters.")
    @Min(value = 20, message = "Field: annotation. Error: must not be less than 20 characters.")
    String annotation;

    @NotBlank(message = "Field: category. Error: must not be blank. Value: null")
    Long categoryId;

    @Max(value = 7000, message = "Field: annotation. Error: must not be more than 7000 characters.")
    @Min(value = 20, message = "Field: annotation. Error: must not be less than 20 characters.")
    String description;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    @NotNull
    Location location;

    Boolean paid;

    Integer participantLimit;

    Boolean requestModeration;

    @NotBlank(message = "Field: annotation. Error: must not be blank. Value: null")
    @Max(value = 120, message = "Field: annotation. Error: must not be more than 120 characters.")
    @Min(value = 3, message = "Field: annotation. Error: must not be less than 3 characters.")
    String title;

}

