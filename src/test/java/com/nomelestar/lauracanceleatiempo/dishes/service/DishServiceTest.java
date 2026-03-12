package com.nomelestar.lauracanceleatiempo.dishes.service;

import com.nomelestar.lauracanceleatiempo.chef.dto.ChefResponseDTO;
import com.nomelestar.lauracanceleatiempo.chef.service.ChefService;
import com.nomelestar.lauracanceleatiempo.dishes.dto.DishCreateDTO;
import com.nomelestar.lauracanceleatiempo.dishes.dto.DishResponseDTO;
import com.nomelestar.lauracanceleatiempo.dishes.models.Dish;
import com.nomelestar.lauracanceleatiempo.dishes.repository.DishRepository;
import com.nomelestar.lauracanceleatiempo.excepciones.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DishServiceTest {

    @Mock
    private DishRepository dishRepository;

    @Mock
    private ChefService chefService;

    @InjectMocks
    private DishService dishService;

    private ChefResponseDTO chefDTO;
    private Dish dish;

    @BeforeEach
    void setUp() {
        chefDTO = new ChefResponseDTO(1L, "British", "Gordon", "Top chef", List.of());

        dish = new Dish();
        dish.setId(10L);
        dish.setName("Beef Wellington");
        dish.setPrice(new BigDecimal("45.00"));

        com.nomelestar.lauracanceleatiempo.chef.model.Chef chefEntity =
                new com.nomelestar.lauracanceleatiempo.chef.model.Chef();
        chefEntity.setId(1L);
        chefEntity.setName("Gordon");
        dish.setChef(chefEntity);
    }

    // ─────────────────────────────────────────────
    // create
    // ─────────────────────────────────────────────

    @Test
    void create_returnsDishResponseDTO() {
        DishCreateDTO dto = new DishCreateDTO("Beef Wellington", new BigDecimal("45.00"), 1L);
        when(chefService.findById(1L)).thenReturn(chefDTO);
        when(dishRepository.save(any(Dish.class))).thenReturn(dish);

        DishResponseDTO result = dishService.create(dto);

        assertThat(result.name()).isEqualTo("Beef Wellington");
        assertThat(result.price()).isEqualByComparingTo("45.00");
        verify(chefService).findById(1L);
        verify(dishRepository).save(any(Dish.class));
    }

    @Test
    void create_throwsNotFound_whenChefDoesNotExist() {
        DishCreateDTO dto = new DishCreateDTO("Pasta", new BigDecimal("12.00"), 99L);
        when(chefService.findById(99L)).thenThrow(new NotFoundException("Chef not found"));

        assertThatThrownBy(() -> dishService.create(dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Chef not found");
    }

    // ─────────────────────────────────────────────
    // findAll
    // ─────────────────────────────────────────────

    @Test
    void findAll_returnsListOfDTOs() {
        when(dishRepository.findAll()).thenReturn(List.of(dish));

        List<DishResponseDTO> result = dishService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Beef Wellington");
    }

    @Test
    void findAll_returnsEmptyList_whenNoDishes() {
        when(dishRepository.findAll()).thenReturn(List.of());

        assertThat(dishService.findAll()).isEmpty();
    }

    // ─────────────────────────────────────────────
    // findById
    // ─────────────────────────────────────────────

    @Test
    void findById_returnsDTOWhenFound() {
        when(dishRepository.findById(10L)).thenReturn(Optional.of(dish));

        DishResponseDTO result = dishService.findById(10L);

        assertThat(result.id()).isEqualTo(10L);
    }

    @Test
    void findById_throwsNotFoundException_whenNotFound() {
        when(dishRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dishService.findById(999L))
                .isInstanceOf(NotFoundException.class);
    }

    // ─────────────────────────────────────────────
    // findByChef
    // ─────────────────────────────────────────────

    @Test
    void findByChef_returnsFilteredDishes() {
        when(dishRepository.findByChefId(1L)).thenReturn(List.of(dish));

        List<DishResponseDTO> result = dishService.findByChef(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).chefId()).isEqualTo(1L);
    }

    @Test
    void findByChef_returnsEmpty_whenNoMatchingDishes() {
        when(dishRepository.findByChefId(99L)).thenReturn(List.of());

        assertThat(dishService.findByChef(99L)).isEmpty();
    }
}
