
package com.edt.dto;

public record ProfesseurResponse(
        Long id,
        String nom,
        String prenom,
        String email,
        String specialite,
        Long departementId,
        String nomDepartement
) {}
