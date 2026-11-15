
package com.edt.dto;

public record FiliereMatiereResponse(
        Long id,
        Long filiereId,
        Long matiereId,
        String matiereNom,
        String semestre,
        Float heuresCours,
        Float heuresTP
) {}
