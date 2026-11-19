
package com.edt.Service;

import com.edt.dto.GroupeRequest;
import com.edt.dto.GroupeResponse;
import com.edt.entity.Groupe;
import com.edt.entity.Filiere;
import com.edt.repository.GroupeRepository;
import com.edt.repository.FiliereRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.HashSet;

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

    // Ajouter un groupe avec ses semestres
    @Transactional
    public GroupeResponse ajouter(GroupeRequest request) {

        Filiere filiere = filiereRepository.findById(request.filiereId())
                .orElseThrow(() -> new RuntimeException("Filière introuvable."));

        Groupe groupe = new Groupe();
        groupe.setNom(request.nom());
        groupe.setEffectif(request.effectif());
        groupe.setFiliere(filiere);

        // Enregistrer les semestres sélectionnés
        groupe.setSemestres(new HashSet<>(request.semestres()));

        return convertir(groupeRepository.save(groupe));
    }

    // Modifier un groupe et ses semestres
    @Transactional
    public GroupeResponse modifier(Long id, GroupeRequest request) {

        Groupe groupe = groupeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Groupe introuvable."));

        Filiere filiere = filiereRepository.findById(request.filiereId())
                .orElseThrow(() -> new RuntimeException("Filière introuvable."));

        groupe.setNom(request.nom());
        groupe.setEffectif(request.effectif());
        groupe.setFiliere(filiere);

        // Remplacer les anciennes associations par les nouvelles
        groupe.setSemestres(new HashSet<>(request.semestres()));

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
                groupe.getFiliere().getNom(),
                groupe.getSemestres().stream().sorted().toList()
        );
    }
}
