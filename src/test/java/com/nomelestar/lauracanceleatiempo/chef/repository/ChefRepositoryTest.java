package com.nomelestar.lauracanceleatiempo.chef.repository;

import com.nomelestar.lauracanceleatiempo.chef.model.Chef;
import com.nomelestar.lauracanceleatiempo.config.StubJwtDecoderConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for ChefRepository using H2 in-memory DB.
 * Note: Spring Boot 4 removed @DataJpaTest slice, so we use @SpringBootTest.
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(StubJwtDecoderConfig.class)
@Transactional
class ChefRepositoryTest {

    @Autowired
    private ChefRepository chefRepository;

    private Chef gordon;

    @BeforeEach
    void setUp() {
        gordon = new Chef();
        gordon.setName("Gordon Ramsay");
        gordon.setSpeciality("British");
        gordon.setDescription("Michelin star chef");
    }

    // ─────────────────────────────────────────────
    // save + findById
    // ─────────────────────────────────────────────

    @Test
    void save_persistsChef() {
        Chef saved = chefRepository.save(gordon);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Gordon Ramsay");
    }

    @Test
    void findById_returnsChef_afterSave() {
        Chef saved = chefRepository.save(gordon);

        Optional<Chef> found = chefRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Gordon Ramsay");
    }

    @Test
    void findById_returnsEmpty_whenNotExist() {
        Optional<Chef> found = chefRepository.findById(9999L);

        assertThat(found).isEmpty();
    }

    // ─────────────────────────────────────────────
    // findAll
    // ─────────────────────────────────────────────

    @Test
    void findAll_includesPersistedChef() {
        chefRepository.save(gordon);

        List<Chef> all = chefRepository.findAll();

        assertThat(all).isNotEmpty();
        assertThat(all).anyMatch(c -> c.getName().equals("Gordon Ramsay"));
    }

    // ─────────────────────────────────────────────
    // findByName
    // ─────────────────────────────────────────────

    @Test
    void findByName_returnsCorrectChef() {
        chefRepository.save(gordon);

        Chef found = chefRepository.findByName("Gordon Ramsay");

        assertThat(found).isNotNull();
        assertThat(found.getSpeciality()).isEqualTo("British");
    }

    @Test
    void findByName_returnsNull_whenNotFound() {
        Chef found = chefRepository.findByName("Nonexistent Chef xyz987");

        assertThat(found).isNull();
    }
}
