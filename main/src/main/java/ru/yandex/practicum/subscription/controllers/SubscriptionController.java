package ru.yandex.practicum.subscription.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.event.dto.EventShortDto;
import ru.yandex.practicum.subscription.dto.SubscriptionDto;
import ru.yandex.practicum.subscription.service.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{subscriberId}/subscriptions")
@RequiredArgsConstructor
@Validated
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<SubscriptionDto> createSubscription(@PathVariable Integer subscriberId,
                                                              @RequestParam(name = "userId") Integer userId) {
        return new ResponseEntity<SubscriptionDto>(subscriptionService.createSubscription(subscriberId, userId), HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteSubscription(@PathVariable Integer subscriberId,
                                                   @RequestParam(name = "subscriptionId") Integer subscriptionId) {
        subscriptionService.deleteSubscription(subscriberId, subscriptionId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionDto>> getAllSubscriptions(
            @PathVariable Integer subscriberId) {
        return ResponseEntity.ok().body(subscriptionService.getAllSubscriptions(subscriberId));
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventShortDto>> getAllSubscriptionsEvents(
            @PathVariable Integer subscriberId) {
        return ResponseEntity.ok().body(subscriptionService.getAllSubscriptionsEvents(subscriberId));
    }

}
