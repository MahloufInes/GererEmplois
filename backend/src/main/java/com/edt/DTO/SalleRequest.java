
package com.edt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SalleRequest(
        @NotBlank String nom,
        @NotNull @Positive Integer capacite,
        @NotBlank String type
) {}
