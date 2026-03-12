package com.nomelestar.lauracanceleatiempo.e2e;

import com.nomelestar.lauracanceleatiempo.chef.dto.ChefCreateDTO;
import com.nomelestar.lauracanceleatiempo.config.StubJwtDecoderConfig;
import com.nomelestar.lauracanceleatiempo.dishes.dto.DishCreateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * E2E test — full flow: create chef → read chef → create dish → read dish.
 * Uses stub JwtDecoder (no real Keycloak needed).
 * Spring Boot 4: @AutoConfigureMockMvc removed, use webAppContextSetup() manually.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@Import(StubJwtDecoderConfig.class)
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
                .apply(springSecurity())
                .build();
    }

    // ── Full flow: Chef → Dish ─────────────────────────────────────────

    @Test
    void fullFlow_createChefThenDish_andReadBoth() throws Exception {

        // 1. POST /api/chefs — ADMIN creates a chef
        ChefCreateDTO chefCreateDTO = new ChefCreateDTO(
                "Gordon Ramsay", "British", "World-class Michelin star chef"
        );

        MvcResult chefResult = mockMvc.perform(post("/api/chefs")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
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

        // 2. GET /api/chefs/{id} — USER reads the chef
        mockMvc.perform(get("/api/chefs/{id}", chefId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(chefId))
                .andExpect(jsonPath("$.name").value("Gordon Ramsay"));

        // 3. POST /api/dishes — ADMIN creates a dish linked to the chef
        DishCreateDTO dishCreateDTO = new DishCreateDTO(
                "Beef Wellington", new BigDecimal("45.99"), chefId
        );

        MvcResult dishResult = mockMvc.perform(post("/api/dishes")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
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

        // 4. GET /api/dishes/{id} — USER reads the dish
        mockMvc.perform(get("/api/dishes/{id}", dishId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dishId))
                .andExpect(jsonPath("$.name").value("Beef Wellington"))
                .andExpect(jsonPath("$.chefId").value(chefId));
    }

    // ── Security edge cases ───────────────────────────────────────────

    @Test
    void postChef_withoutAuthentication_returns401() throws Exception {
        ChefCreateDTO dto = new ChefCreateDTO("Anonymous", "None", "No auth");
        mockMvc.perform(post("/api/chefs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void postChef_withUserRole_returns403() throws Exception {
        ChefCreateDTO dto = new ChefCreateDTO("Gordon", "British", "Star chef");
        mockMvc.perform(post("/api/chefs")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getChef_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/chefs/99999")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
