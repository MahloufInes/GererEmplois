
package com.edt.dto;

import java.util.List;

public record GroupeResponse(
        Long id,
        String nom,
        Integer effectif,
        Long filiereId,
        String filiere,
        List<String> semestres
) {}
