package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Event;

public interface EventRepository extends JpaRepository <Event, Long> {
}
