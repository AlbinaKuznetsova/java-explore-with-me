package ru.yandex.practicum.subscription;

import org.mapstruct.Mapper;
import ru.yandex.practicum.subscription.dto.SubscriptionDto;
import ru.yandex.practicum.subscription.model.Subscription;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {
    SubscriptionDto toSubscriptionDto(Subscription subscription);

    List<SubscriptionDto> toSubscriptionDto(List<Subscription> subscriptions);
}
