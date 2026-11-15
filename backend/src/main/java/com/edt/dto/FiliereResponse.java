
package com.edt.dto;

import java.util.List;

public record FiliereResponse(
        Long id,
        String nom,
        Long departementId,
        List<FiliereMatiereResponse> matieres
) {}
