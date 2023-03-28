package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Event;
import ru.practicum.model.Like;
import ru.practicum.model.User;

import java.util.List;

public interface LikesRepository extends JpaRepository<Like, Long> {

    Like findByEventAndUser(Event event, User user);


    List<Like> findByUserAndIsLikeIsTrue(User user);

}
