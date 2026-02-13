package com.nomelestar.lauracanceleatiempo.chef.controller;

import com.nomelestar.lauracanceleatiempo.chef.dto.ChefCreateDTO;
import com.nomelestar.lauracanceleatiempo.chef.dto.ChefResponseDTO;
import com.nomelestar.lauracanceleatiempo.chef.service.ChefService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chefs")
public class ChefController {
    private final ChefService chefService;
    public ChefController(ChefService chefService) {
        this.chefService = chefService;
    }
    @GetMapping
    public List<ChefResponseDTO> findAll(){
        return chefService.findAll();
    }
    @GetMapping("/{id}")
    public ChefResponseDTO findById(@PathVariable Long id){
        return chefService.findById(id);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChefResponseDTO create(@Valid @RequestBody ChefCreateDTO dto){
        return chefService.create(dto);
    }
}
