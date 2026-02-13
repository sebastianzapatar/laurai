package com.nomelestar.lauracanceleatiempo.chef.mapper;

import com.nomelestar.lauracanceleatiempo.chef.dto.ChefCreateDTO;
import com.nomelestar.lauracanceleatiempo.chef.dto.ChefResponseDTO;
import com.nomelestar.lauracanceleatiempo.chef.model.Chef;

public class ChefMapper {
    private ChefMapper() {
    }
    public static ChefResponseDTO toChefResponseDTO(Chef chef) {
        return new ChefResponseDTO(
                chef.getId(),
                chef.getSpeciality(),
                chef.getName(),
                chef.getDescription()
        );

    }
    public static Chef toChef(ChefCreateDTO dto) {
        Chef chef = new Chef();
        chef.setName(dto.name());
        chef.setDescription(dto.description());
        chef.setSpeciality(dto.specialty());
        return chef;
    }
}
