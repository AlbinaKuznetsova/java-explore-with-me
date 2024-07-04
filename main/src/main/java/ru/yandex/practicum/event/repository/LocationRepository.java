package ru.yandex.practicum.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.event.dto.Location;

public interface LocationRepository extends JpaRepository<Location, Integer> {
}
