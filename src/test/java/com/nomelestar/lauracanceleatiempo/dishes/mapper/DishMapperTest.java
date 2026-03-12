package com.nomelestar.lauracanceleatiempo.dishes.mapper;

import com.nomelestar.lauracanceleatiempo.chef.model.Chef;
import com.nomelestar.lauracanceleatiempo.dishes.dto.DishCreateDTO;
import com.nomelestar.lauracanceleatiempo.dishes.dto.DishResponseDTO;
import com.nomelestar.lauracanceleatiempo.dishes.models.Dish;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class DishMapperTest {

    // ─────────────────────────────────────────────
    // toEntity
    // ─────────────────────────────────────────────

    @Test
    void toEntity_mapsAllFields() {
        Chef chef = new Chef();
        chef.setId(1L);
        chef.setName("Gordon");

        DishCreateDTO dto = new DishCreateDTO("Pasta", new BigDecimal("15.99"), 1L);

        Dish dish = DishMapper.toEntity(dto, chef);

        assertThat(dish.getName()).isEqualTo("Pasta");
        assertThat(dish.getPrice()).isEqualByComparingTo("15.99");
        assertThat(dish.getChef()).isEqualTo(chef);
    }

    // ─────────────────────────────────────────────
    // updateEntity
    // ─────────────────────────────────────────────

    @Test
    void updateEntity_updatesAllFields() {
        Chef originalChef = new Chef();
        originalChef.setId(1L);

        Dish dish = new Dish();
        dish.setName("Old name");
        dish.setPrice(new BigDecimal("5.00"));
        dish.setChef(originalChef);

        Chef newChef = new Chef();
        newChef.setId(2L);
        DishCreateDTO dto = new DishCreateDTO("New name", new BigDecimal("25.00"), 2L);

        DishMapper.updateEntity(dish, dto, newChef);

        assertThat(dish.getName()).isEqualTo("New name");
        assertThat(dish.getPrice()).isEqualByComparingTo("25.00");
        assertThat(dish.getChef()).isEqualTo(newChef);
    }

    // ─────────────────────────────────────────────
    // toDto
    // ─────────────────────────────────────────────

    @Test
    void toDto_withChef_returnsChefFields() {
        Chef chef = new Chef();
        chef.setId(10L);
        chef.setName("Julia");

        Dish dish = new Dish();
        dish.setId(100L);
        dish.setName("Croissant");
        dish.setPrice(new BigDecimal("3.50"));
        dish.setChef(chef);

        DishResponseDTO dto = DishMapper.toDto(dish);

        assertThat(dto.id()).isEqualTo(100L);
        assertThat(dto.name()).isEqualTo("Croissant");
        assertThat(dto.price()).isEqualByComparingTo("3.50");
        assertThat(dto.chefId()).isEqualTo(10L);
        assertThat(dto.chefName()).isEqualTo("Julia");
    }

    @Test
    void toDto_withNullChef_returnsNullChefFields() {
        Dish dish = new Dish();
        dish.setId(200L);
        dish.setName("Orphan Dish");
        dish.setPrice(new BigDecimal("1.00"));
        dish.setChef(null);

        DishResponseDTO dto = DishMapper.toDto(dish);

        assertThat(dto.chefId()).isNull();
        assertThat(dto.chefName()).isNull();
    }
}
