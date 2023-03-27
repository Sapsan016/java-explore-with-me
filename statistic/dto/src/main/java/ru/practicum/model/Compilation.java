package ru.practicum.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.events.EventShortDto;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "compilations")
public class Compilation {
    @Id
    @Column(name = "compilation_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Transient
    List<EventShortDto> events;

    Boolean pinned;

    String title;



}
