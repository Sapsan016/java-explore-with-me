package ru.practicum.dto.hit;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;


@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class HitDto {

    String app;
    String uri;
    Integer hits;

}
