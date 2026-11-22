
package com.edt.dto;

public record DisponibiliteResponse(
        Long id,
        Long creneauId,
        String jour,
        String heureDebut,
        String heureFin
) {}
