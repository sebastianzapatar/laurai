package com.nomelestar.lauracanceleatiempo.chef.mapper;

import com.nomelestar.lauracanceleatiempo.chef.dto.ChefCreateDTO;
import com.nomelestar.lauracanceleatiempo.chef.dto.ChefResponseDTO;
import com.nomelestar.lauracanceleatiempo.chef.model.Chef;
import com.nomelestar.lauracanceleatiempo.dishes.models.Dish;

import java.util.ArrayList;
import java.util.List;

public class ChefMapper {
    private ChefMapper() {
    }
    public static ChefResponseDTO toChefResponseDTO(Chef chef) {
        List<Long> dishesIds;
        if (chef.getDishes() == null) {
            dishesIds = new ArrayList<>();
        }else{
            dishesIds=chef.getDishes().stream()
                    .map(Dish::getId).toList();
        }
        return new ChefResponseDTO(
                chef.getId(),
                chef.getSpeciality(),
                chef.getName(),
                chef.getDescription(),
                dishesIds
        );

    }
    public static Chef toChef(ChefCreateDTO dto) {
        Chef chef = new Chef();
        chef.setName(dto.name());
        chef.setDescription(dto.description());
        chef.setSpeciality(dto.specialty());
        return chef;
    }
    public static Chef toChefResponse(ChefResponseDTO dto) {
        Chef chef = new Chef();
        chef.setId(dto.id());
        chef.setName(dto.name());
        chef.setDescription(dto.description());
        chef.setSpeciality(dto.specialty());
        return chef;
    }
}
