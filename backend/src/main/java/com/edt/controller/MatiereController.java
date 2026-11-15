package com.edt.controller;

import com.edt.Service.MatiereService;
import com.edt.dto.MatiereRequest;
import com.edt.dto.MatiereResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matieres")
@CrossOrigin(origins = "http://localhost:4200")
public class MatiereController {

    private final MatiereService matiereService;

    public MatiereController(MatiereService matiereService) {
        this.matiereService = matiereService;
    }

    @GetMapping
    public List<MatiereResponse> getAll() {
        return matiereService.getAllMatieres();
    }

    @GetMapping("/departement/{departementId}")
    public List<MatiereResponse> getByDepartement(@PathVariable Long departementId) {
        return matiereService.getMatieresByDepartement(departementId);
    }

    @GetMapping("/{id}")
    public MatiereResponse getById(@PathVariable Long id) {
        return matiereService.getMatiereById(id);
    }

    @GetMapping("/search")
    public List<MatiereResponse> search(@RequestParam String nom) {
        return matiereService.searchMatieres(nom);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MatiereResponse ajouter(@RequestBody MatiereRequest request) {
        return matiereService.ajouterMatiere(request);
    }

    @PutMapping("/{id}")
    public MatiereResponse modifier(@PathVariable Long id,
                                    @RequestBody MatiereRequest request) {
        return matiereService.modifierMatiere(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        matiereService.supprimerMatiere(id);
    }
}