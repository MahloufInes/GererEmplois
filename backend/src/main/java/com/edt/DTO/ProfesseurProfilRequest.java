
package com.edt.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ProfesseurProfilRequest(

        @NotBlank
        String nom,

        @NotBlank
        String prenom,

        @NotBlank
        @Email
        String email,

        @NotBlank
        String specialite,

        String motDePasse

) {}
