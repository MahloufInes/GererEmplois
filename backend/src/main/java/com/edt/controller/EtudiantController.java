
package com.edt.controller;

import com.edt.dto.EtudiantRequest;
import com.edt.dto.EtudiantResponse;
import com.edt.service.EtudiantService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/etudiants")
@CrossOrigin(origins = "http://localhost:4200")
public class EtudiantController {

    private final EtudiantService etudiantService;

    public EtudiantController(EtudiantService etudiantService) {
        this.etudiantService = etudiantService;
    }

    @GetMapping
    public List<EtudiantResponse> afficherTous() {
        return etudiantService.afficherTous();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EtudiantResponse ajouter(
            @Valid @RequestBody EtudiantRequest request
    ) {
        return etudiantService.ajouter(request);
    }
}
