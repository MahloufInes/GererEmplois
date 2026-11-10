package com.edt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FiliereRequest(
        @NotBlank String nom,
        @NotNull Long departementId
) {}