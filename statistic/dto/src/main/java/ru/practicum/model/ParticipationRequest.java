package ru.practicum.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.events.states.RequestState;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "requests")
public class ParticipationRequest {
    @Id
    @Column(name = "request_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    LocalDateTime created;

    @Column(name = "event_id")
    Long event;

    @Column(name = "user_id")
    Long requester;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    RequestState status;


}
