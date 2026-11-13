package com.edt.controller;

import com.edt.dto.FiliereRequest;
import com.edt.dto.FiliereResponse;
import com.edt.Service.FiliereService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/filieres")
@CrossOrigin(origins = "http://localhost:4200")
public class FiliereController {

    private final FiliereService filiereService;

    public FiliereController(FiliereService filiereService) {
        this.filiereService = filiereService;
    }

    @GetMapping
    public List<FiliereResponse> afficherToutes() {
        return filiereService.afficherToutes();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FiliereResponse ajouter(
            @Valid @RequestBody FiliereRequest request) {
        return filiereService.ajouter(request);
    }
}