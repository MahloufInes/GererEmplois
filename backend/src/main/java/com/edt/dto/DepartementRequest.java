
package com.edt.dto;

import jakarta.validation.constraints.NotBlank;

public record DepartementRequest(
        @NotBlank String code,
        @NotBlank String nom
) {}
