package com.edt.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GroupeRequest(
        @NotBlank String nom,
        @NotNull @Min(1) Integer effectif,
        @NotNull Long filiereId
) {}