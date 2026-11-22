
package com.edt.dto;

import jakarta.validation.constraints.NotNull;

public record DisponibiliteRequest(
        @NotNull Long creneauId
) {}
