package com.edt.dto;

public record EtudiantResponse(
        Long id,
        String nom,
        String prenom,
        String email,
        String matricule,
        Long groupeId,
        String groupe,
        String filiere
) {}