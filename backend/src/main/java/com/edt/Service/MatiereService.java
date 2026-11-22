package com.edt.Service;

import com.edt.dto.MatiereRequest;
import com.edt.dto.MatiereResponse;
import com.edt.entity.Matiere;
import com.edt.entity.Departement;
import com.edt.repository.MatiereRepository;
import com.edt.repository.DepartementRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MatiereService {

    private final MatiereRepository matiereRepository;
    private final DepartementRepository departementRepository;

    public MatiereService(
            MatiereRepository matiereRepository,
            DepartementRepository departementRepository) {
        this.matiereRepository = matiereRepository;
        this.departementRepository = departementRepository;
    }
    public List<MatiereResponse> getMatieresByGroupe(Long groupeId) {
        return matiereRepository.findMatieresByGroupeId(groupeId)
                .stream()
                .map(this::toResponse)
                .toList();
    }
    // Convert entity to response DTO
    private MatiereResponse toResponse(Matiere matiere) {
        Long departementId = null;
        String departementNom = null;

        if (matiere.getDepartement() != null) {
            departementId = matiere.getDepartement().getId();
            departementNom = matiere.getDepartement().getNom();
        }

        return new MatiereResponse(
                matiere.getId(),
                matiere.getNom(),
                departementId,
                departementNom
        );
    }

    // Get all matieres
    public List<MatiereResponse> getAllMatieres() {
        return matiereRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Get matieres by department
    public List<MatiereResponse> getMatieresByDepartement(Long departementId) {
        if (!departementRepository.existsById(departementId)) {
            throw new RuntimeException("Département introuvable");
        }

        return matiereRepository.findByDepartementId(departementId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Get one matiere
    public MatiereResponse getMatiereById(Long id) {
        Matiere matiere = matiereRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Matière introuvable"));

        return toResponse(matiere);
    }

    // Search matieres by name
    public List<MatiereResponse> searchMatieres(String nom) {
        return matiereRepository.findByNomContainingIgnoreCase(nom)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Add a matiere
    public MatiereResponse ajouterMatiere(MatiereRequest request) {

        if (request.nom() == null || request.nom().isBlank()) {
            throw new RuntimeException("Le nom de la matière est obligatoire");
        }

        Departement departement = departementRepository
                .findById(request.departementId())
                .orElseThrow(() ->
                        new RuntimeException("Département introuvable"));

        Matiere matiere = new Matiere();
        matiere.setNom(request.nom().trim());
        matiere.setDepartement(departement);

        return toResponse(matiereRepository.save(matiere));
    }

    // Update a matiere
    public MatiereResponse modifierMatiere(
            Long id, MatiereRequest request) {

        if (request.nom() == null || request.nom().isBlank()) {
            throw new RuntimeException("Le nom de la matière est obligatoire");
        }

        Matiere matiere = matiereRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Matière introuvable"));

        Departement departement = departementRepository
                .findById(request.departementId())
                .orElseThrow(() ->
                        new RuntimeException("Département introuvable"));

        matiere.setNom(request.nom().trim());
        matiere.setDepartement(departement);

        return toResponse(matiereRepository.save(matiere));
    }

    // Delete a matiere
    public void supprimerMatiere(Long id) {

        Matiere matiere = matiereRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Matière introuvable"));

        if (!matiere.getEnseignements().isEmpty()
                || !matiere.getPreferencesProfesseurs().isEmpty()) {
            throw new RuntimeException(
                    "Impossible de supprimer cette matière : elle est déjà utilisée.");
        }

        matiereRepository.delete(matiere);
    }
}