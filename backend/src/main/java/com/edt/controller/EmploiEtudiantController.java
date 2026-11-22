
package com.edt.controller;

import com.edt.dto.SeanceResponse;
import com.edt.entity.Etudiant;
import com.edt.entity.Seance;
import com.edt.repository.EtudiantRepository;
import com.edt.repository.SeanceRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/etudiants")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class EmploiEtudiantController {

    private final EtudiantRepository etudiantRepository;
    private final SeanceRepository seanceRepository;

    @GetMapping("/mon-emploi")
    @Transactional(readOnly = true)
    public List<SeanceResponse> afficherMonEmploi(
            Authentication authentication) {

        Etudiant etudiant = etudiantRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Étudiant introuvable"
                ));

        if (etudiant.getGroupe() == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Aucun groupe n'est associé à cet étudiant"
            );
        }

        List<Seance> seances =
                seanceRepository.findSeancesPublieesByGroupeId(
                        etudiant.getGroupe().getId()
                );

        return seances.stream()
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
