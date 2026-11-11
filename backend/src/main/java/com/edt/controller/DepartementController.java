
package com.edt.controller;

import com.edt.dto.DepartementRequest;
import com.edt.dto.DepartementResponse;
import com.edt.service.DepartementService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departements")
@CrossOrigin(origins = "http://localhost:4200")
public class DepartementController {

    private final DepartementService departementService;

    public DepartementController(DepartementService departementService) {
        this.departementService = departementService;
    }

    @GetMapping
    public List<DepartementResponse> afficherTous() {
        return departementService.afficherTous();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepartementResponse ajouter(
            @Valid @RequestBody DepartementRequest request) {
        return departementService.ajouter(request);
    }

    @PutMapping("/{id}")
    public DepartementResponse modifier(
            @PathVariable Long id,
            @Valid @RequestBody DepartementRequest request) {
        return departementService.modifier(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        departementService.supprimer(id);
    }
}
