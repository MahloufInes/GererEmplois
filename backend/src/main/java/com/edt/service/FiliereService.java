package com.edt.Service;

import com.edt.dto.FiliereRequest;
import com.edt.dto.FiliereResponse;
import com.edt.entity.Filiere;
import com.edt.entity.Departement;
import com.edt.repository.FiliereRepository;
import com.edt.repository.DepartementRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FiliereService {

    private final FiliereRepository filiereRepository;
    private final DepartementRepository departementRepository;

    public FiliereService(
            FiliereRepository filiereRepository,
            DepartementRepository departementRepository) {
        this.filiereRepository = filiereRepository;
        this.departementRepository = departementRepository;
    }

    @Transactional(readOnly = true)
    public List<FiliereResponse> afficherToutes() {
        return filiereRepository.findAll()
                .stream()
                .map(f -> new FiliereResponse(f.getId(), f.getNom()))
                .toList();
    }

    @Transactional
    public FiliereResponse ajouter(FiliereRequest request) {

        if (filiereRepository.existsByNom(request.nom())) {
            throw new RuntimeException("Cette filière existe déjà.");
        }

        Departement departement = departementRepository
                .findById(request.departementId())
                .orElseThrow(() -> new RuntimeException("Département introuvable."));

        Filiere filiere = new Filiere();
        filiere.setNom(request.nom());
        filiere.setDepartement(departement);

        Filiere saved = filiereRepository.save(filiere);

        return new FiliereResponse(saved.getId(), saved.getNom());
    }
}