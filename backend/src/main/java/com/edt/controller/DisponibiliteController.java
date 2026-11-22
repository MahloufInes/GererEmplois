
package com.edt.controller;

import com.edt.dto.DisponibiliteRequest;
import com.edt.dto.DisponibiliteResponse;
import com.edt.service.DisponibiliteService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/professeurs/mes-disponibilites")
@CrossOrigin(origins = "http://localhost:4200")
public class DisponibiliteController {

    private final DisponibiliteService disponibiliteService;

    public DisponibiliteController(DisponibiliteService disponibiliteService) {
        this.disponibiliteService = disponibiliteService;
    }

    @GetMapping
    public List<DisponibiliteResponse> afficher(
            Authentication authentication) {
        return disponibiliteService.afficher(authentication.getName());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DisponibiliteResponse ajouter(
            Authentication authentication,
            @Valid @RequestBody DisponibiliteRequest request) {
        return disponibiliteService.ajouter(
                authentication.getName(), request);
    }

    @PutMapping("/{id}")
    public DisponibiliteResponse modifier(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody DisponibiliteRequest request) {
        return disponibiliteService.modifier(
                authentication.getName(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(
            Authentication authentication,
            @PathVariable Long id) {
        disponibiliteService.supprimer(authentication.getName(), id);
    }
}
