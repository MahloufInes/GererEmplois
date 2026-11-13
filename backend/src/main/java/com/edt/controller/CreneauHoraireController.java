
package com.edt.controller;

import com.edt.dto.CreneauHoraireRequest;
import com.edt.dto.CreneauHoraireResponse;
import com.edt.service.CreneauHoraireService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/creneaux-horaires")
@CrossOrigin(origins = "http://localhost:4200")
public class CreneauHoraireController {

    private final CreneauHoraireService service;

    public CreneauHoraireController(CreneauHoraireService service) {
        this.service = service;
    }

    @GetMapping
    public List<CreneauHoraireResponse> afficherTous() {
        return service.afficherTous();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreneauHoraireResponse ajouter(
            @Valid @RequestBody CreneauHoraireRequest request) {
        return service.ajouter(request);
    }

    @PutMapping("/{id}")
    public CreneauHoraireResponse modifier(
            @PathVariable Long id,
            @Valid @RequestBody CreneauHoraireRequest request) {
        return service.modifier(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        service.supprimer(id);
    }
}
