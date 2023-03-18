package ru.practicum.admin;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.AddCatDto;
import ru.practicum.dto.category.CatDto;
import ru.practicum.dto.category.CatMapper;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;


    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CatDto addCategory(@RequestBody @Valid AddCatDto category) {
        return CatMapper.toCatDto(adminService.createCategory(category));

    }
}
