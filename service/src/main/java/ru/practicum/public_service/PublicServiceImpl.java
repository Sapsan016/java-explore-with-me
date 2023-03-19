package ru.practicum.public_service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.model.Category;
import ru.practicum.repositories.CategoryRepository;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PublicServiceImpl implements PublicService {

    CategoryRepository categoryRepository;

    public PublicServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    @Override
    public List<Category> getCategories(Integer from, Integer size) {
        return categoryRepository.getAllCategories(from, size);
    }
}
