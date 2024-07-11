package ru.yandex.practicum.subscription.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.subscription.model.Subscription;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
    Optional<Subscription> findBySubscriberIdAndUserId(Integer subscriberId, Integer userId);

    List<Subscription> findAllBySubscriberId(Integer subscriberId);
}
