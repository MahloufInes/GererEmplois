package com.edt.dto;

public record GroupeResponse(
        Long id,
        String nom,
        Integer effectif,
        Long filiereId,
        String filiere
) {}