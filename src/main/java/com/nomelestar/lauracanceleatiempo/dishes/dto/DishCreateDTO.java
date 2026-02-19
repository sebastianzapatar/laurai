package com.nomelestar.lauracanceleatiempo.dishes.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record DishCreateDTO(
        @NotBlank(message = "please plase my fucking darling")
        @Size(min=2,max=140)
        String name,
        @NotBlank(message = "price is required, Dyllan Ladron")
        @DecimalMin(value = "0.00", inclusive = false,
        message="price must be greater than 0")
        BigDecimal price,
        @NotNull(message = "required ")
        @Positive(message = "chefId has to be positive mf")
        Long chefId
) {
}
