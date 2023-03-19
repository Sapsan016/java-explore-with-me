package ru.practicum.admin;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.category.AddCatDto;
import ru.practicum.dto.category.CatMapper;
import ru.practicum.dto.users.AddUserDto;
import ru.practicum.dto.users.UserMapper;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.model.Category;
import ru.practicum.model.User;
import ru.practicum.repositories.CategoryRepository;
import ru.practicum.repositories.UserRepository;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AdminServiceImpl implements AdminService {

    CategoryRepository categoryRepository;
    UserRepository userRepository;

    public AdminServiceImpl(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Category createCategory(AddCatDto addCatDto) {
        Category category = CatMapper.toCat(addCatDto);
        categoryRepository.save(category);
        log.info("Добавлена категория с Id = {}", category.getId());
        return category;
    }

    @Override
    public void removeCategory(Long catId) {
        Category catToRemove = findCategoryById(catId);
        categoryRepository.delete(catToRemove);
        log.info("Удалена категория с Id = {}", catId);
    }

    @Override
    public Category alterCategory(Long catId, AddCatDto category) {
        Category catToAlter = findCategoryById(catId);
        catToAlter.setName(category.getName());
        categoryRepository.save(catToAlter);
        log.info("Изменена категория с Id = {}", catToAlter.getId());
        return catToAlter;
    }

    @Override
    public User addUser(AddUserDto addUserDto) {
        User user = UserMapper.toUser(addUserDto);
        userRepository.save(user);
        log.info("Добавлена категория с Id = {}", user.getId());
        return user;
    }

    @Override
    public void removeUser(Long userId) {
        User userToRemove = findUserById(userId);
        userRepository.delete(userToRemove);
        log.info("Удален пользователь с Id = {}", userId);
    }


    private Category findCategoryById(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Category with id=%s was not found", catId)));
    }
    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("User with id=%s was not found", userId)));
    }
}
