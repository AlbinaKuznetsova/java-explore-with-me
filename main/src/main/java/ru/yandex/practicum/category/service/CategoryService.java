package ru.yandex.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.category.CategoryMapper;
import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.category.dto.NewCategoryDto;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.category.repository.CategoryRepository;
import ru.yandex.practicum.exceptions.ObjectNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        return categoryMapper.toCategoryDto(categoryRepository.findAllFromSize(from, size));
    }

    public CategoryDto getCategory(int catId) {
        if (categoryRepository.findById(catId).isEmpty()) {
            log.info("Category with id={} was not found", catId);
            throw new ObjectNotFoundException("Category with id=" + catId + " was not found", new Throwable("The required object was not found."));
        } else {
            return categoryMapper.toCategoryDto(categoryRepository.findById(catId).get());
        }
    }

    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        return categoryMapper.toCategoryDto(
                categoryRepository.save(categoryMapper.toCategory(newCategoryDto)));
    }

    public void deleteCategory(int catId) {
        Optional<Category> optionalCategory = categoryRepository.findById(catId);
        if (optionalCategory.isPresent()) {
            categoryRepository.deleteById(catId);
        } else {
            log.info("Category with id={} was not found", catId);
            throw new ObjectNotFoundException("Category with id=" + catId + " was not found", new Throwable("The required object was not found."));
        }

    }

    public CategoryDto updateCategory(int catId,
                                      NewCategoryDto newCategoryDto) {
        Category categoryFromDb = null;
        Optional<Category> optionalCategory = categoryRepository.findById(catId);
        if (optionalCategory.isPresent()) {
            categoryFromDb = optionalCategory.get();
        } else {
            log.info("Category with id={} was not found", catId);
            throw new ObjectNotFoundException("Category with id=" + catId + " was not found", new Throwable("The required object was not found."));
        }
        if (newCategoryDto.getName() != null) {
            categoryFromDb.setName(newCategoryDto.getName());
        }
        return categoryMapper.toCategoryDto(categoryRepository.save(categoryFromDb));
    }
}
