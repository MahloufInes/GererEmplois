package com.edt.dto;

public record LoginResponse(
        String token,
        Long id,
        String nom,
        String prenom,
        String email,
        String role
) {}