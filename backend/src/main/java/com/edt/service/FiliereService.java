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
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public FiliereResponse ajouter(FiliereRequest request) {

        if (filiereRepository.existsByNom(request.nom())) {
            throw new RuntimeException("Cette filière existe déjà.");
        }

        Departement departement = departementRepository
                .findById(request.departementId())
                .orElseThrow(() ->
                        new RuntimeException("Département introuvable."));

        Filiere filiere = new Filiere();
        filiere.setNom(request.nom());
        filiere.setDepartement(departement);

        return toResponse(filiereRepository.save(filiere));
    }

    @Transactional
    public FiliereResponse modifier(Long id, FiliereRequest request) {

        Filiere filiere = filiereRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Filière introuvable."));

        if (filiereRepository.existsByNom(request.nom())
                && !filiere.getNom().equals(request.nom())) {
            throw new RuntimeException("Cette filière existe déjà.");
        }

        Departement departement = departementRepository
                .findById(request.departementId())
                .orElseThrow(() ->
                        new RuntimeException("Département introuvable."));

        filiere.setNom(request.nom());
        filiere.setDepartement(departement);

        return toResponse(filiereRepository.save(filiere));
    }

    @Transactional
    public void supprimer(Long id) {

        Filiere filiere = filiereRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Filière introuvable."));

        if (!filiere.getGroupes().isEmpty()) {
            throw new RuntimeException(
                    "Impossible de supprimer cette filière : elle possède des groupes.");
        }

        filiereRepository.delete(filiere);
    }

    private FiliereResponse toResponse(Filiere filiere) {
        return new FiliereResponse(
                filiere.getId(),
                filiere.getNom(),
                filiere.getDepartement().getId()
        );
    }
}
