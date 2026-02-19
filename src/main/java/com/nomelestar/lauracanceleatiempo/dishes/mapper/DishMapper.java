package com.nomelestar.lauracanceleatiempo.dishes.mapper;

import com.nomelestar.lauracanceleatiempo.chef.model.Chef;
import com.nomelestar.lauracanceleatiempo.dishes.dto.DishCreateDTO;
import com.nomelestar.lauracanceleatiempo.dishes.dto.DishResponseDTO;
import com.nomelestar.lauracanceleatiempo.dishes.models.Dish;

public class DishMapper {
    private DishMapper() {}
    public static Dish toEntity(DishCreateDTO dto, Chef chef) {
        Dish dish = new Dish();
        dish.setName(dto.name());
        dish.setPrice(dto.price());
        dish.setChef(chef);
        return dish;
    }
    public static void updateEntity(Dish dish, DishCreateDTO dto, Chef chef) {
        dish.setName(dto.name());
        dish.setPrice(dto.price());
        dish.setChef(chef);
    }
    public static DishResponseDTO toDto(Dish dish) {
        Long chefId=null;
        String chefName=null;
        if(dish.getChef()!=null) {
            chefId=dish.getChef().getId();
            chefName=dish.getChef().getName();
        }
        return  new DishResponseDTO(
                dish.getId(),
                dish.getName(),
                dish.getPrice(),
                chefId,
                chefName
        );
    }
}
