package com.nomelestar.lauracanceleatiempo.chef.mapper;

import com.nomelestar.lauracanceleatiempo.chef.dto.ChefCreateDTO;
import com.nomelestar.lauracanceleatiempo.chef.dto.ChefResponseDTO;
import com.nomelestar.lauracanceleatiempo.chef.model.Chef;
import com.nomelestar.lauracanceleatiempo.dishes.models.Dish;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ChefMapperTest {

    // ─────────────────────────────────────────────
    // toChef
    // ─────────────────────────────────────────────

    @Test
    void toChef_mapsAllFieldsCorrectly() {
        ChefCreateDTO dto = new ChefCreateDTO("Gordon Ramsay", "British", "Hell's Kitchen expert");

        Chef chef = ChefMapper.toChef(dto);

        assertThat(chef.getName()).isEqualTo("Gordon Ramsay");
        assertThat(chef.getSpeciality()).isEqualTo("British");
        assertThat(chef.getDescription()).isEqualTo("Hell's Kitchen expert");
    }

    // ─────────────────────────────────────────────
    // toChefResponseDTO
    // ─────────────────────────────────────────────

    @Test
    void toChefResponseDTO_withDishes_returnsDishIds() {
        Chef chef = new Chef();
        chef.setId(1L);
        chef.setName("Gordon");
        chef.setSpeciality("British");
        chef.setDescription("Top chef");

        Dish dish1 = new Dish();
        dish1.setId(10L);
        Dish dish2 = new Dish();
        dish2.setId(20L);
        chef.setDishes(List.of(dish1, dish2));

        ChefResponseDTO dto = ChefMapper.toChefResponseDTO(chef);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.name()).isEqualTo("Gordon");
        assertThat(dto.specialty()).isEqualTo("British");
        assertThat(dto.description()).isEqualTo("Top chef");
        assertThat(dto.dishesId()).containsExactlyInAnyOrder(10L, 20L);
    }

    @Test
    void toChefResponseDTO_withNullDishes_returnsEmptyList() {
        Chef chef = new Chef();
        chef.setId(2L);
        chef.setName("Julia");
        chef.setSpeciality("French");
        chef.setDescription("Classic cook");
        chef.setDishes(null);

        ChefResponseDTO dto = ChefMapper.toChefResponseDTO(chef);

        assertThat(dto.dishesId()).isEmpty();
    }

    // ─────────────────────────────────────────────
    // toChefResponse (from DTO back to entity)
    // ─────────────────────────────────────────────

    @Test
    void toChefResponse_mapsFieldsFromResponseDTO() {
        ChefResponseDTO dto = new ChefResponseDTO(5L, "Italian", "Mario", "Pizza master", List.of());

        Chef chef = ChefMapper.toChefResponse(dto);

        assertThat(chef.getId()).isEqualTo(5L);
        assertThat(chef.getName()).isEqualTo("Mario");
        assertThat(chef.getSpeciality()).isEqualTo("Italian");
        assertThat(chef.getDescription()).isEqualTo("Pizza master");
    }
}
