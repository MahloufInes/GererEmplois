
package com.edt.service;

import com.edt.entity.Matiere;
import com.edt.entity.Professeur;
import com.edt.entity.PreferenceMatiere;
import com.edt.repository.MatiereRepository;
import com.edt.repository.ProfesseurRepository;
import com.edt.repository.PreferenceMatiereRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PreferenceMatiereService {

    private final PreferenceMatiereRepository preferenceRepository;
    private final ProfesseurRepository professeurRepository;
    private final MatiereRepository matiereRepository;

    // Add a subject to a professor
    public PreferenceMatiere ajouterPreference(
            Long professeurId,
            Long matiereId
    ) {
        Professeur professeur = professeurRepository.findById(professeurId)
                .orElseThrow(() ->
                        new RuntimeException("Professeur introuvable"));

        Matiere matiere = matiereRepository.findById(matiereId)
                .orElseThrow(() ->
                        new RuntimeException("Matiere introuvable"));

        if (preferenceRepository.existsByProfesseurIdAndMatiereId(
                professeurId, matiereId)) {
            throw new RuntimeException(
                    "Cette matière est déjà associée à ce professeur");
        }

        PreferenceMatiere preference = PreferenceMatiere.builder()
                .professeur(professeur)
                .matiere(matiere)
                .build();

        return preferenceRepository.save(preference);
    }

    // Get all subjects of a professor
    @Transactional(readOnly = true)
    public List<PreferenceMatiere> getByProfesseur(Long professeurId) {

        if (!professeurRepository.existsById(professeurId)) {
            throw new RuntimeException("Professeur introuvable");
        }

        return preferenceRepository.findByProfesseurId(professeurId);
    }

    // Get all professors associated with a subject
    @Transactional(readOnly = true)
    public List<PreferenceMatiere> getByMatiere(Long matiereId) {

        if (!matiereRepository.existsById(matiereId)) {
            throw new RuntimeException("Matiere introuvable");
        }

        return preferenceRepository.findByMatiereId(matiereId);
    }

    // Remove one subject from a professor
    public void supprimerPreference(
            Long professeurId,
            Long matiereId
    ) {
        if (!preferenceRepository.existsByProfesseurIdAndMatiereId(
                professeurId, matiereId)) {
            throw new RuntimeException(
                    "Cette association professeur-matière n'existe pas");
        }

        preferenceRepository.deleteByProfesseurIdAndMatiereId(
                professeurId, matiereId);
    }
}
