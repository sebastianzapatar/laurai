package com.nomelestar.lauracanceleatiempo.dishes.dto;

import java.math.BigDecimal;

public record DishResponseDTO(
        Long id,
        String name,
        BigDecimal price,
        Long chefId,
        String chefName
) {
}
