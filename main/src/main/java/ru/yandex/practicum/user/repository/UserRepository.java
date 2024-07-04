package ru.yandex.practicum.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query(value = "select * from users as u ORDER BY u.id LIMIT ?2 OFFSET ?1", nativeQuery = true)
    List<User> findAllFromSize(Integer from, Integer size);

    @Query(value = "select * from users as u where u.id IN (?1) ORDER BY u.id LIMIT ?3 OFFSET ?2", nativeQuery = true)
    List<User> findAllByIdsFromSize(Integer[] ids, Integer from, Integer size);
}
