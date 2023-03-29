package ru.practicum.mappers;

import ru.practicum.dto.events.likes.LikesDto;
import ru.practicum.model.Like;

public class LikeMapper {


    public static LikesDto toDto(Like like) {
        return new LikesDto(
                like.getId(),
                EventMapper.toEventShortDto(like.getEvent()),
                UserMapper.toUserShortDto(like.getUser()),
                like.getIsLike()

        );
    }

}
