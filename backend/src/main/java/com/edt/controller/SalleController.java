
package com.edt.controller;

import com.edt.dto.SalleRequest;
import com.edt.dto.SalleResponse;
import com.edt.service.SalleService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salles")
@CrossOrigin(origins = "http://localhost:4200")
public class SalleController {

    private final SalleService salleService;

    public SalleController(SalleService salleService) {
        this.salleService = salleService;
    }

    @GetMapping
    public List<SalleResponse> afficherToutes() {
        return salleService.afficherToutes();
    }

    @GetMapping("/count")
    public long compterSalles() {
        return salleService.compterSalles();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SalleResponse ajouter(
            @Valid @RequestBody SalleRequest request) {
        return salleService.ajouter(request);
    }

    @PutMapping("/{id}")
    public SalleResponse modifier(
            @PathVariable Long id,
            @Valid @RequestBody SalleRequest request) {
        return salleService.modifier(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        salleService.supprimer(id);
    }
}
