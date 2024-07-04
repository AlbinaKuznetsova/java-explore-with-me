package ru.yandex.practicum.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.category.model.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    @Query(value = "select * from category as c ORDER BY c.name LIMIT ?2 OFFSET ?1", nativeQuery = true)
    List<Category> findAllFromSize(Integer from, Integer size);
}
