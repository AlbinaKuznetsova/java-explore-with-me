package ru.yandex.practicum.event.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "requests")
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "created_date")
    private LocalDateTime created = LocalDateTime.now();
    @Column(name = "event_id")
    private Integer event;
    @Column(name = "requester_id")
    private Integer requester;
    @Column(name = "status")
    private String status;
}
