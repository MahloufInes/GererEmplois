package com.edt.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EtudiantRequest(
        @NotBlank String nom,
        @NotBlank String prenom,
        @Email @NotBlank String email,
        @NotBlank String motDePasse,
        @NotBlank String matricule,
        @NotNull Long groupeId
) {}