
package com.edt.controller;

import com.edt.dto.SeanceResponse;
import com.edt.repository.SeanceRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consultation")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ConsultationEmploiDuTempsController {

    private final SeanceRepository seanceRepository;

    @GetMapping("/groupe/{groupeId}/semestre/{semestre}")
    @Transactional(readOnly = true)
    public ResponseEntity<List<SeanceResponse>> consulterEmploiDuTemps(
            @PathVariable Long groupeId,
            @PathVariable String semestre) {

        List<SeanceResponse> seances =
                seanceRepository
                        .findByGroupeIdAndEmploiDuTempsSemestre(
                                groupeId,
                                semestre
                        )
                        .stream()
                        .map(s -> new SeanceResponse(
                                s.getId(),
                                s.getDate() != null
                                        ? s.getDate().toString()
                                        : null,
                                s.getCreneau().getJour().name(),
                                s.getCreneau().getHeureDebut().toString(),
                                s.getCreneau().getHeureFin().toString(),
                                s.getType() != null
                                        ? s.getType().name()
                                        : null,
                                s.getStatut() != null
                                        ? s.getStatut().name()
                                        : null,
                                s.getMatiere().getNom(),
                                s.getProfesseur().getNom(),
                                s.getSalle().getNom(),
                                s.getGroupe().getNom()
                        ))
                        .toList();

        return ResponseEntity.ok(seances);
    }
}
