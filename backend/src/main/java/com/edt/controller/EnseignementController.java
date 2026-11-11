
package com.edt.controller;

import com.edt.dto.EnseignementDTO;
import com.edt.service.EnseignementService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enseignements")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class EnseignementController {

    private final EnseignementService enseignementService;

    // CREATE
    @PostMapping
    public EnseignementDTO ajouterEnseignement(
            @RequestParam Long professeurId,
            @RequestParam Long matiereId,
            @RequestParam Long filiereId,
            @RequestParam Float heuresCours,
            @RequestParam Float heuresTP) {

        return enseignementService.ajouterEnseignement(
                professeurId, matiereId, filiereId,
                heuresCours, heuresTP
        );
    }

    // GET ALL
    @GetMapping
    public List<EnseignementDTO> getAllEnseignements() {
        return enseignementService.getAllEnseignements();
    }

    // GET BY ID
    @GetMapping("/{id}")
    public EnseignementDTO getEnseignementById(
            @PathVariable Long id) {

        return enseignementService.getEnseignementById(id);
    }

    // GET BY PROFESSOR
    @GetMapping("/professeur/{professeurId}")
    public List<EnseignementDTO> getByProfesseur(
            @PathVariable Long professeurId) {

        return enseignementService.getByProfesseur(professeurId);
    }

    // GET BY FILIERE
    @GetMapping("/filiere/{filiereId}")
    public List<EnseignementDTO> getByFiliere(
            @PathVariable Long filiereId) {

        return enseignementService.getByFiliere(filiereId);
    }

    // UPDATE HOURS
    @PutMapping("/{id}/heures")
    public EnseignementDTO modifierHeures(
            @PathVariable Long id,
            @RequestParam Float heuresCours,
            @RequestParam Float heuresTP) {

        return enseignementService.modifierHeures(
                id, heuresCours, heuresTP
        );
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void supprimerEnseignement(
            @PathVariable Long id) {

        enseignementService.supprimerEnseignement(id);
    }
}
