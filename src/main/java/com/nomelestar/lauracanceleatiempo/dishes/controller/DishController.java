package com.nomelestar.lauracanceleatiempo.dishes.controller;

import com.nomelestar.lauracanceleatiempo.dishes.dto.DishCreateDTO;
import com.nomelestar.lauracanceleatiempo.dishes.dto.DishResponseDTO;
import com.nomelestar.lauracanceleatiempo.dishes.service.DishService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dishes")
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping
    public List<DishResponseDTO> findAll() {
        return dishService.findAll();
    }

    @GetMapping("/{id}")
    public DishResponseDTO findById(@PathVariable Long id) {
        return dishService.findById(id);
    }

    @GetMapping("/chef/{chefId}")
    public List<DishResponseDTO> findByChef(@PathVariable Long chefId) {
        return dishService.findByChef(chefId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DishResponseDTO create(@Valid @RequestBody DishCreateDTO dto) {
        return dishService.create(dto);
    }
}
