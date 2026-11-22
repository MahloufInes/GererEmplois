
package com.edt.controller;

import com.edt.dto.ProfesseurCreationResponse;
import com.edt.dto.ProfesseurProfilRequest;
import com.edt.dto.ProfesseurRequest;
import com.edt.dto.ProfesseurResponse;
import com.edt.service.ProfesseurService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/professeurs")
@CrossOrigin(origins = "http://localhost:4200")
public class ProfesseurController {

    private final ProfesseurService professeurService;

    public ProfesseurController(ProfesseurService professeurService) {
        this.professeurService = professeurService;
    }

    // Afficher tous les professeurs
    @GetMapping
    public List<ProfesseurResponse> afficherTous() {
        return professeurService.afficherTous();
    }

    // Afficher le nombre de professeurs
    @GetMapping("/count")
    public long compterProfesseurs() {
        return professeurService.compterProfesseurs();
    }

    // Ajouter un professeur
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProfesseurCreationResponse ajouter(
            @Valid @RequestBody ProfesseurRequest request) {
        return professeurService.ajouter(request);
    }

    // Afficher le profil du professeur connecté
    @GetMapping("/mon-profil")
    public ProfesseurResponse afficherMonProfil(
            Authentication authentication) {

        return professeurService.afficherMonProfil(
                authentication.getName()
        );
    }

    // Modifier le profil du professeur connecté
    @PutMapping("/mon-profil")
    public ProfesseurResponse modifierMonProfil(
            Authentication authentication,
            @Valid @RequestBody ProfesseurProfilRequest request) {

        return professeurService.modifierMonProfil(
                authentication.getName(),
                request
        );
    }

    // Modifier un professeur
    @PutMapping("/{id}")
    public ProfesseurResponse modifier(
            @PathVariable Long id,
            @Valid @RequestBody ProfesseurRequest request) {
        return professeurService.modifier(id, request);
    }

    // Supprimer un professeur
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        professeurService.supprimer(id);
    }
}
