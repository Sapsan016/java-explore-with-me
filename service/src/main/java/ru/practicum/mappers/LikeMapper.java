package ru.practicum.mappers;

import ru.practicum.dto.events.likes.LikeDto;
import ru.practicum.model.Like;

public class LikeMapper {


    public static LikeDto toDto(Like like) {
        return new LikeDto(
                like.getId(),
                EventMapper.toEventShortDto(like.getEvent()),
                UserMapper.toUserShortDto(like.getUser()),
                like.getIsLike()

        );
    }

}
