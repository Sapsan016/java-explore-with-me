package ru.practicum.mappers;

import ru.practicum.dto.events.requests.ParticipationRequestDto;
import ru.practicum.model.ParticipationRequest;

import java.time.format.DateTimeFormatter;

public class RequestMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static ParticipationRequestDto toDto(ParticipationRequest request) {
        return new ParticipationRequestDto(
                request.getCreated().format(FORMATTER),
                request.getEvent(),
                request.getId(),
                request.getRequester(),
                request.getStatus().toString()

        );
    }
}
