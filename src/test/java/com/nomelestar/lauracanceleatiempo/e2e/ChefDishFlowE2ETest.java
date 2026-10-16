package com.nomelestar.lauracanceleatiempo.e2e;

import com.nomelestar.lauracanceleatiempo.chef.dto.ChefCreateDTO;
import com.nomelestar.lauracanceleatiempo.dishes.dto.DishCreateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * E2E test — full flow: create chef → read chef → create dish → read dish.
 * Spring Boot 4: @AutoConfigureMockMvc removed, use webAppContextSetup() manually.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@Transactional
class ChefDishFlowE2ETest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    // ── Full flow: Chef → Dish ─────────────────────────────────────────

    @Test
    void fullFlow_createChefThenDish_andReadBoth() throws Exception {

        // 1. POST /api/chefs — create a chef
        ChefCreateDTO chefCreateDTO = new ChefCreateDTO(
                "Gordon Ramsay", "British", "World-class Michelin star chef"
        );

        MvcResult chefResult = mockMvc.perform(post("/api/chefs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chefCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Gordon Ramsay"))
                .andExpect(jsonPath("$.specialty").value("British"))
                .andReturn();

        Map<?, ?> chefBody = objectMapper.readValue(
                chefResult.getResponse().getContentAsString(), Map.class);
        Long chefId = Long.valueOf(chefBody.get("id").toString());
        assertThat(chefId).isPositive();

        // 2. GET /api/chefs/{id} — read the chef
        mockMvc.perform(get("/api/chefs/{id}", chefId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(chefId))
                .andExpect(jsonPath("$.name").value("Gordon Ramsay"));

        // 3. POST /api/dishes — create a dish linked to the chef
        DishCreateDTO dishCreateDTO = new DishCreateDTO(
                "Beef Wellington", new BigDecimal("45.99"), chefId
        );

        MvcResult dishResult = mockMvc.perform(post("/api/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dishCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Beef Wellington"))
                .andExpect(jsonPath("$.chefId").value(chefId))
                .andExpect(jsonPath("$.chefName").value("Gordon Ramsay"))
                .andReturn();

        Map<?, ?> dishBody = objectMapper.readValue(
                dishResult.getResponse().getContentAsString(), Map.class);
        Long dishId = Long.valueOf(dishBody.get("id").toString());
        assertThat(dishId).isPositive();

        // 4. GET /api/dishes/{id} — read the dish
        mockMvc.perform(get("/api/dishes/{id}", dishId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dishId))
                .andExpect(jsonPath("$.name").value("Beef Wellington"))
                .andExpect(jsonPath("$.chefId").value(chefId));
    }

    @Test
    void getChef_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/chefs/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
