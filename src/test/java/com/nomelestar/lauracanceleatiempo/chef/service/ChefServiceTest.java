package com.nomelestar.lauracanceleatiempo.chef.service;

import com.nomelestar.lauracanceleatiempo.chef.dto.ChefCreateDTO;
import com.nomelestar.lauracanceleatiempo.chef.dto.ChefResponseDTO;
import com.nomelestar.lauracanceleatiempo.chef.model.Chef;
import com.nomelestar.lauracanceleatiempo.chef.repository.ChefRepository;
import com.nomelestar.lauracanceleatiempo.excepciones.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChefServiceTest {

    @Mock
    private ChefRepository chefRepository;

    @InjectMocks
    private ChefService chefService;

    private Chef chef;

    @BeforeEach
    void setUp() {
        chef = new Chef();
        chef.setId(1L);
        chef.setName("Gordon Ramsay");
        chef.setSpeciality("British");
        chef.setDescription("Michelin star chef");
        chef.setDishes(List.of());
    }

    // ─────────────────────────────────────────────
    // create
    // ─────────────────────────────────────────────

    @Test
    void create_savesAndReturnsDTO() {
        ChefCreateDTO dto = new ChefCreateDTO("Gordon Ramsay", "British", "Michelin star chef");
        when(chefRepository.save(any(Chef.class))).thenReturn(chef);

        ChefResponseDTO result = chefService.create(dto);

        assertThat(result.name()).isEqualTo("Gordon Ramsay");
        assertThat(result.specialty()).isEqualTo("British");
        verify(chefRepository, times(1)).save(any(Chef.class));
    }

    // ─────────────────────────────────────────────
    // findAll
    // ─────────────────────────────────────────────

    @Test
    void findAll_returnsListOfDTOs() {
        Chef chef2 = new Chef();
        chef2.setId(2L);
        chef2.setName("Julia Child");
        chef2.setSpeciality("French");
        chef2.setDescription("Classic cook");
        chef2.setDishes(List.of());

        when(chefRepository.findAll()).thenReturn(List.of(chef, chef2));

        List<ChefResponseDTO> result = chefService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Gordon Ramsay");
        assertThat(result.get(1).name()).isEqualTo("Julia Child");
    }

    @Test
    void findAll_returnsEmptyList_whenNoChefsExist() {
        when(chefRepository.findAll()).thenReturn(List.of());

        List<ChefResponseDTO> result = chefService.findAll();

        assertThat(result).isEmpty();
    }

    // ─────────────────────────────────────────────
    // findById
    // ─────────────────────────────────────────────

    @Test
    void findById_returnsDTOWhenFound() {
        when(chefRepository.findById(1L)).thenReturn(Optional.of(chef));

        ChefResponseDTO result = chefService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Gordon Ramsay");
    }

    @Test
    void findById_throwsNotFoundException_whenNotFound() {
        when(chefRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chefService.findById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Chef not found");
    }
}
