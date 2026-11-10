package com.edt.service;

import com.edt.dto.GroupeRequest;
import com.edt.dto.GroupeResponse;
import com.edt.entity.Groupe;
import com.edt.entity.Filiere;
import com.edt.repository.GroupeRepository;
import com.edt.repository.FiliereRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GroupeService {

    private final GroupeRepository groupeRepository;
    private final FiliereRepository filiereRepository;

    public GroupeService(
            GroupeRepository groupeRepository,
            FiliereRepository filiereRepository
    ) {
        this.groupeRepository = groupeRepository;
        this.filiereRepository = filiereRepository;
    }

    @Transactional(readOnly = true)
    public List<GroupeResponse> afficherTous() {
        return groupeRepository.findAll()
                .stream()
                .map(this::convertir)
                .toList();
    }

    @Transactional
    public GroupeResponse ajouter(GroupeRequest request) {

        Filiere filiere = filiereRepository.findById(request.filiereId())
                .orElseThrow(() -> new RuntimeException("Filière introuvable."));

        Groupe groupe = new Groupe();
        groupe.setNom(request.nom());
        groupe.setEffectif(request.effectif());
        groupe.setFiliere(filiere);

        return convertir(groupeRepository.save(groupe));
    }

    @Transactional
    public GroupeResponse modifier(Long id, GroupeRequest request) {

        Groupe groupe = groupeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Groupe introuvable."));

        Filiere filiere = filiereRepository.findById(request.filiereId())
                .orElseThrow(() -> new RuntimeException("Filière introuvable."));

        groupe.setNom(request.nom());
        groupe.setEffectif(request.effectif());
        groupe.setFiliere(filiere);

        return convertir(groupeRepository.save(groupe));
    }

    @Transactional
    public void supprimer(Long id) {

        Groupe groupe = groupeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Groupe introuvable."));

        if (!groupe.getEtudiants().isEmpty() || !groupe.getSeances().isEmpty()) {
            throw new RuntimeException(
                    "Impossible de supprimer ce groupe : il contient des étudiants ou des séances."
            );
        }

        groupeRepository.delete(groupe);
    }

    private GroupeResponse convertir(Groupe groupe) {
        return new GroupeResponse(
                groupe.getId(),
                groupe.getNom(),
                groupe.getEffectif(),
                groupe.getFiliere().getId(),
                groupe.getFiliere().getNom()
        );
    }
}