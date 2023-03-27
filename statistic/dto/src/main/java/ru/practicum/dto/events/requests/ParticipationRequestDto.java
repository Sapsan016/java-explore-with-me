package ru.practicum.dto.events.requests;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationRequestDto implements Comparable<ParticipationRequestDto> {

    String created;

    Long event;

    Long id;

    Long requester;

    String status;


    @Override
    public int compareTo(ParticipationRequestDto otherDto) {
        return Long.compare(getId(), otherDto.getId());
    }
}
