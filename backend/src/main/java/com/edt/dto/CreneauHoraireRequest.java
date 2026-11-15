
package com.edt.dto;

import com.edt.entity.enums.Jour;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public record CreneauHoraireRequest(
        @NotNull Jour jour,
        @NotNull LocalTime heureDebut,
        @NotNull LocalTime heureFin
) {}
