
package com.edt.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.edt.dto.CreneauHoraireRequest;
import com.edt.dto.CreneauHoraireResponse;
import com.edt.service.CreneauHoraireService;

import jakarta.validation.Valid;

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
    CreneauHoraireResponse modifier(
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
