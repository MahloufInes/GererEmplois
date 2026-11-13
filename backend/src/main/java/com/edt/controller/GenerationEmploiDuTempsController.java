
package com.edt.controller;

import com.edt.Service.GenerationEmploiDuTempsService;
import com.edt.entity.EmploiDuTemps;
import com.edt.repository.SeanceRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/generation")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class GenerationEmploiDuTempsController {

    private final GenerationEmploiDuTempsService generationService;
    private final SeanceRepository seanceRepository;

    @PostMapping("/generer")
    public ResponseEntity<?> genererEmploiDuTemps(
            @RequestParam Long filiereId,
            @RequestParam String semestre,
            @RequestParam Long administrateurId) {

        EmploiDuTemps emploi =
                generationService.genererEmploiDuTemps(
                        filiereId,
                        semestre,
                        administrateurId
                );

        int nombreSeances =
                seanceRepository.findByEmploiDuTempsId(
                        emploi.getId()
                ).size();

        return ResponseEntity.ok(
                Map.of(
                        "message", "Emploi du temps généré avec succès",
                        "emploiDuTempsId", emploi.getId(),
                        "semestre", emploi.getSemestre(),
                        "nombreSeances", nombreSeances
                )
        );
    }
}
