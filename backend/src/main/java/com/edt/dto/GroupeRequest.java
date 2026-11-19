
package com.edt.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record GroupeRequest(
        @NotBlank String nom,

        @NotNull @Min(1) Integer effectif,

        @NotNull Long filiereId,

        @NotEmpty
        List<@Pattern(regexp = "S[1-6]", message = "Semestre invalide") String> semestres
) {}
