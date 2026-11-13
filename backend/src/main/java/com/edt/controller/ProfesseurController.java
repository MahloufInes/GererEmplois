
package com.edt.controller;

import com.edt.dto.ProfesseurRequest;
import com.edt.dto.ProfesseurResponse;
import com.edt.service.ProfesseurService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ProfesseurResponse ajouter(
            @Valid @RequestBody ProfesseurRequest request) {
        return professeurService.ajouter(request);
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
