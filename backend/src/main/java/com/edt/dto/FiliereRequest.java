
package com.edt.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record FiliereRequest(
        @NotBlank String nom,
        @NotNull Long departementId,
        @NotNull List<@Valid FiliereMatiereRequest> matieres
) {}
