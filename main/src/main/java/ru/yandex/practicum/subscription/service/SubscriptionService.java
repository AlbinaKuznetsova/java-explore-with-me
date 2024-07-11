package ru.yandex.practicum.subscription.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.event.dto.EventShortDto;
import ru.yandex.practicum.event.service.EventService;
import ru.yandex.practicum.exceptions.ForbiddenOperationException;
import ru.yandex.practicum.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.subscription.SubscriptionMapper;
import ru.yandex.practicum.subscription.dto.SubscriptionDto;
import ru.yandex.practicum.subscription.model.Subscription;
import ru.yandex.practicum.subscription.repository.SubscriptionRepository;
import ru.yandex.practicum.user.model.User;
import ru.yandex.practicum.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class SubscriptionService {
    private final SubscriptionRepository repository;
    private final UserService userService;
    private final SubscriptionMapper subscriptionMapper;
    private final EventService eventService;

    public SubscriptionDto createSubscription(Integer subscriberId, Integer userId) {
        if (repository.findBySubscriberIdAndUserId(subscriberId, userId).isPresent()) {
            throw new ForbiddenOperationException("Subscription already exists", new Throwable("Forbidden"));
        }
        Subscription subscription = new Subscription();
        User subscriber = userService.getUserById(subscriberId);
        User user = userService.getUserById(userId);
        subscription.setSubscriber(subscriber);
        subscription.setUser(user);
        return subscriptionMapper.toSubscriptionDto(repository.save(subscription));
    }

    public void deleteSubscription(Integer subscriberId, Integer subscriptionId) {
        Optional<Subscription> subscriptionOptional = repository.findById(subscriptionId);
        if (subscriptionOptional.isEmpty()) {
            throw new ObjectNotFoundException("Subscription is not found", new Throwable("The required object was not found."));
        } else if (!subscriptionOptional.get().getSubscriber().getId().equals(subscriberId)) {
            throw new ForbiddenOperationException("Forbidden for this user", new Throwable("Forbidden"));
        } else {
            repository.deleteById(subscriptionId);
        }
    }

    public List<SubscriptionDto> getAllSubscriptions(Integer subscriberId) {
        return subscriptionMapper.toSubscriptionDto(repository.findAllBySubscriberId(subscriberId));
    }

    public List<EventShortDto> getAllSubscriptionsEvents(Integer subscriberId) {
        List<Subscription> subscriptions = repository.findAllBySubscriberId(subscriberId);
        List<Integer> users = new ArrayList<>();
        for (Subscription subscription : subscriptions) {
            users.add(subscription.getUser().getId());
        }
        return eventService.findAllByInitiatorIdIn(users);
    }
}
