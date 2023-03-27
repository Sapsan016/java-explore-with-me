package ru.practicum.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.events.states.EventState;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "events")
public class Event {
    @Id
    @Column(name = "event_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String annotation;

    @JoinColumn(name = "category_id")
    @ManyToOne
    Category category;

    @Column(name = "confirmed_requests")
    Integer confirmedRequests;

    @Column(name = "created_on")
    LocalDateTime createdOn;

    String description;

    @Column(name = "event_date")
    LocalDateTime eventDate;

    @JoinColumn(name = "initiator_id")
    @ManyToOne
    User user;

    @JoinColumn(name = "location_id")
    @ManyToOne
    Location location;

    Boolean paid;

    @Column(name = "participant_limit")
    Integer participantLimit;

    @Column(name = "published_on")
    LocalDateTime publishedOn;

    @Column(name = "request_moderation")
    Boolean requestModeration;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    EventState state;

    String title;

    Integer views;


    @Override
    public String toString() {
        return String.format("Event{ " +
                        "ID=%s, annotation='%s, category=%s, confirmedRequests=%s, createdOn=%s, description='%s," +
                        "eventDate=%s, user=%s, location=%s, paid=%s, participantLimit=%s, publishedOn=%s, " +
                        "requestModeration=%s, state=%s, title='%s, views=%s",
                id, annotation, category, confirmedRequests, createdOn, description, eventDate, user, location,
                paid, participantLimit, publishedOn, requestModeration, state, title, views);
    }
}
