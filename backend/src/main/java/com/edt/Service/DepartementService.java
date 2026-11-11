
package com.edt.service;

import com.edt.dto.DepartementRequest;
import com.edt.dto.DepartementResponse;
import com.edt.entity.Departement;
import com.edt.repository.DepartementRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartementService {

    private final DepartementRepository departementRepository;

    public DepartementService(DepartementRepository departementRepository) {
        this.departementRepository = departementRepository;
    }

    @Transactional(readOnly = true)
    public List<DepartementResponse> afficherTous() {
        return departementRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public DepartementResponse ajouter(DepartementRequest request) {

        if (departementRepository.existsByCode(request.code())) {
            throw new RuntimeException("Ce code existe déjà.");
        }

        Departement departement = new Departement();
        departement.setCode(request.code());
        departement.setNom(request.nom());

        return toResponse(departementRepository.save(departement));
    }

    @Transactional
    public DepartementResponse modifier(Long id, DepartementRequest request) {

        Departement departement = departementRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Département introuvable."));

        if (departementRepository.existsByCode(request.code())
                && !departement.getCode().equals(request.code())) {
            throw new RuntimeException("Ce code existe déjà.");
        }

        departement.setCode(request.code());
        departement.setNom(request.nom());

        return toResponse(departementRepository.save(departement));
    }

    @Transactional
    public void supprimer(Long id) {

        Departement departement = departementRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Département introuvable."));

        if (!departement.getFilieres().isEmpty()
                || !departement.getProfesseurs().isEmpty()
                || !departement.getMatieres().isEmpty()) {
            throw new RuntimeException(
                    "Impossible de supprimer ce département : il est utilisé.");
        }

        departementRepository.delete(departement);
    }

    private DepartementResponse toResponse(Departement departement) {
        return new DepartementResponse(
                departement.getId(),
                departement.getCode(),
                departement.getNom()
        );
    }
}
