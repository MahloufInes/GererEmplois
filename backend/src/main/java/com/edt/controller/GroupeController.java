
package com.edt.controller;

import com.edt.dto.GroupeRequest;
import com.edt.dto.GroupeResponse;
import com.edt.service.GroupeService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groupes")
@CrossOrigin(origins = "http://localhost:4200")
public class GroupeController {

    private final GroupeService groupeService;

    public GroupeController(GroupeService groupeService) {
        this.groupeService = groupeService;
    }

    @GetMapping
    public List<GroupeResponse> afficherTous() {
        return groupeService.afficherTous();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GroupeResponse ajouter(
            @Valid @RequestBody GroupeRequest request
    ) {
        return groupeService.ajouter(request);
    }

    @PutMapping("/{id}")
    public GroupeResponse modifier(
            @PathVariable Long id,
            @Valid @RequestBody GroupeRequest request
    ) {
        return groupeService.modifier(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        groupeService.supprimer(id);
    }
}
