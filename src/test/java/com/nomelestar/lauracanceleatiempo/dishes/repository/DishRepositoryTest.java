package com.nomelestar.lauracanceleatiempo.dishes.repository;

import com.nomelestar.lauracanceleatiempo.chef.model.Chef;
import com.nomelestar.lauracanceleatiempo.chef.repository.ChefRepository;
import com.nomelestar.lauracanceleatiempo.config.StubJwtDecoderConfig;
import com.nomelestar.lauracanceleatiempo.dishes.models.Dish;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for DishRepository using H2 in-memory DB.
 * Note: Spring Boot 4 removed @DataJpaTest slice, so we use @SpringBootTest.
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(StubJwtDecoderConfig.class)
@Transactional
class DishRepositoryTest {

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private ChefRepository chefRepository;

    private Chef chef;

    @BeforeEach
    void setUp() {
        Chef c = new Chef();
        c.setName("Gordon Ramsay");
        c.setSpeciality("British");
        c.setDescription("Michelin chef");
        chef = chefRepository.save(c);
    }

    // ─────────────────────────────────────────────
    // save
    // ─────────────────────────────────────────────

    @Test
    void save_persistsDish() {
        Dish dish = new Dish();
        dish.setName("Beef Wellington");
        dish.setPrice(new BigDecimal("45.00"));
        dish.setChef(chef);

        Dish saved = dishRepository.save(dish);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Beef Wellington");
        assertThat(saved.getChef().getId()).isEqualTo(chef.getId());
    }

    // ─────────────────────────────────────────────
    // findAll
    // ─────────────────────────────────────────────

    @Test
    void findAll_includesPersistedDishes() {
        Dish d1 = new Dish();
        d1.setName("Beef Wellington");
        d1.setPrice(new BigDecimal("45.00"));
        d1.setChef(chef);

        Dish d2 = new Dish();
        d2.setName("Scrambled Eggs");
        d2.setPrice(new BigDecimal("8.00"));
        d2.setChef(chef);

        dishRepository.save(d1);
        dishRepository.save(d2);

        assertThat(dishRepository.findAll()).hasSizeGreaterThanOrEqualTo(2);
    }

    // ─────────────────────────────────────────────
    // findByChefId
    // ─────────────────────────────────────────────

    @Test
    void findByChefId_returnsOnlyMatchingDishes() {
        Chef julia = new Chef();
        julia.setName("Julia Child");
        julia.setSpeciality("French");
        julia.setDescription("French chef");
        julia = chefRepository.save(julia);

        Dish gordonDish = new Dish();
        gordonDish.setName("Beef Wellington");
        gordonDish.setPrice(new BigDecimal("45.00"));
        gordonDish.setChef(chef);

        Dish juliaDish = new Dish();
        juliaDish.setName("Croissant");
        juliaDish.setPrice(new BigDecimal("3.50"));
        juliaDish.setChef(julia);

        dishRepository.save(gordonDish);
        dishRepository.save(juliaDish);

        List<Dish> gordonDishes = dishRepository.findByChefId(chef.getId());

        assertThat(gordonDishes).hasSize(1);
        assertThat(gordonDishes.get(0).getName()).isEqualTo("Beef Wellington");
    }

    @Test
    void findByChefId_returnsEmpty_whenChefHasNoDishes() {
        List<Dish> result = dishRepository.findByChefId(chef.getId());

        assertThat(result).isEmpty();
    }
}
