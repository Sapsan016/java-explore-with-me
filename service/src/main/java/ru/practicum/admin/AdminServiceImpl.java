package ru.practicum.admin;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.category.AddCatDto;
import ru.practicum.dto.category.CatMapper;
import ru.practicum.exception.CategoryNotFoundException;
import ru.practicum.model.Category;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AdminServiceImpl implements AdminService {

    AdminRepository adminRepository;

    public AdminServiceImpl(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public Category createCategory(AddCatDto addCatDto) {
        Category category = CatMapper.toCat(addCatDto);
        adminRepository.save(category);
        log.info("Добавлена категория с Id = {}", category.getId());
        return category;
    }

    @Override
    public void removeCategory(Long itemId) {
        Category catToRemove = findCategoryById(itemId);
        adminRepository.delete(catToRemove);
        log.info("Удалена категория с Id = {}", itemId);

    }

    private Category findCategoryById(Long catId) {
        return adminRepository.findById(catId).orElseThrow(() ->
                new CategoryNotFoundException(String.format("Category with id=%s was not found", catId)));
    }
}
