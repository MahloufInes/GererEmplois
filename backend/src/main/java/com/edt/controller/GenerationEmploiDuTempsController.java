package com.edt.controller;

import com.edt.service.GenerationEmploiDuTempsService;
import com.edt.dto.SeanceResponse;
import com.edt.entity.EmploiDuTemps;
import com.edt.repository.SeanceRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

        EmploiDuTemps emploi = generationService.genererEmploiDuTemps(
                filiereId,
                semestre,
                administrateurId
        );

        int nombreSeances = seanceRepository
                .findByEmploiDuTempsId(emploi.getId())
                .size();

        return ResponseEntity.ok(
                Map.of(
                        "message", "Emploi du temps généré avec succès",
                        "emploiDuTempsId", emploi.getId(),
                        "semestre", emploi.getSemestre(),
                        "nombreSeances", nombreSeances
                )
        );
    }

    @GetMapping("/emploi/{emploiId}/seances")
    @Transactional(readOnly = true)
    public List<SeanceResponse> getSeances(@PathVariable Long emploiId) {

        return seanceRepository.findByEmploiDuTempsId(emploiId)
                .stream()
                .map(s -> new SeanceResponse(
                        s.getId(),
                        s.getDate() != null ? s.getDate().toString() : null,
                        String.valueOf(s.getCreneau().getJour()),
                        String.valueOf(s.getCreneau().getHeureDebut()),
                        String.valueOf(s.getCreneau().getHeureFin()),
                        s.getType() != null ? s.getType().name() : null,
                        s.getStatut() != null ? s.getStatut().name() : null,
                        s.getMatiere().getNom(),
                        s.getProfesseur().getNom(),
                        s.getSalle().getNom(),
                        s.getGroupe().getNom()
                ))
                .toList();
    }

    @GetMapping("/professeur/{professeurId}/seances")
    @Transactional(readOnly = true)
    public List<SeanceResponse> getSeancesProfesseur(
            @PathVariable Long professeurId) {

        return seanceRepository.findByProfesseurId(professeurId)
                .stream()
                .map(s -> new SeanceResponse(
                        s.getId(),
                        s.getDate() != null ? s.getDate().toString() : null,
                        String.valueOf(s.getCreneau().getJour()),
                        String.valueOf(s.getCreneau().getHeureDebut()),
                        String.valueOf(s.getCreneau().getHeureFin()),
                        s.getType() != null ? s.getType().name() : null,
                        s.getStatut() != null ? s.getStatut().name() : null,
                        s.getMatiere().getNom(),
                        s.getProfesseur().getNom(),
                        s.getSalle().getNom(),
                        s.getGroupe().getNom()
                ))
                .toList();
    }
}