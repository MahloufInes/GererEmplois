
package com.edt.controller;

import com.edt.entity.PreferenceMatiere;
import com.edt.service.PreferenceMatiereService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/preferences-matieres")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PreferenceMatiereController {

    private final PreferenceMatiereService preferenceService;

    // Add a subject to a professor
    @PostMapping
    public PreferenceMatiere ajouterPreference(
            @RequestParam Long professeurId,
            @RequestParam Long matiereId) {

        return preferenceService.ajouterPreference(
                professeurId,
                matiereId
        );
    }

    // Get all subjects associated with a professor
    @GetMapping("/professeur/{professeurId}")
    public List<PreferenceMatiere> getByProfesseur(
            @PathVariable Long professeurId) {

        return preferenceService.getByProfesseur(professeurId);
    }

    // Get all professors associated with a subject
    @GetMapping("/matiere/{matiereId}")
    public List<PreferenceMatiere> getByMatiere(
            @PathVariable Long matiereId) {

        return preferenceService.getByMatiere(matiereId);
    }

    // Remove a subject from a professor
    @DeleteMapping
    public void supprimerPreference(
            @RequestParam Long professeurId,
            @RequestParam Long matiereId) {

        preferenceService.supprimerPreference(
                professeurId,
                matiereId
        );
    }
}
