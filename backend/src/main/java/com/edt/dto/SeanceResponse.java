package com.edt.dto;

public record SeanceResponse(
        Long id,
        String date,
        String jour,
        String heureDebut,
        String heureFin,
        String type,
        String statut,
        String matiereNom,
        String professeurNom,
        String salleNom,
        String groupeNom
) {}