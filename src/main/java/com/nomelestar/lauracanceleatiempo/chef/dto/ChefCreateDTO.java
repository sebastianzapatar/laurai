package com.nomelestar.lauracanceleatiempo.chef.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChefCreateDTO(
        @NotBlank(message = "Laura Isabel cancele por amor de Dios")
        @Size(min=2,max=120,message = "Dylan Ladron")
        String name,
        @Size(max=80,message = "Sofia does not cry")
        String specialty,
        @Size(max = 100,message = "Mafe Mafe Mafe")
        String description
) {
}
