package com.edt.dto;

import com.edt.entity.enums.TypeSalle;

public record SalleResponse(
        Long id,
        String nom,
        Integer capacite,
        TypeSalle type
) {}