package ru.yandex.practicum.subscription.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yandex.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "subscriptions")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "subscriber_id")
    private User subscriber; // подписчик
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // пользователь, на которого подписались
    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();
}
