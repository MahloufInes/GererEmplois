
package com.edt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record FiliereMatiereRequest(

        @NotNull
        Long matiereId,

        @NotBlank
        String semestre,

        @NotNull
        @PositiveOrZero
        Float heuresCours,

        @NotNull
        @PositiveOrZero
        Float heuresTP

) {}
