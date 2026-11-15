
package com.edt.dto;

import com.edt.entity.enums.Jour;
import java.time.LocalTime;

public record CreneauHoraireResponse(
        Long id,
        Jour jour,
        LocalTime heureDebut,
        LocalTime heureFin
) {}
