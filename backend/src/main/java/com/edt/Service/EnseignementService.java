
package com.edt.Service;

import com.edt.entity.*;
import com.edt.DTO.EnseignementDTO;
import com.edt.repository.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EnseignementService {

    private final EnseignementRepository enseignementRepository;
    private final ProfesseurRepository professeurRepository;
    private final MatiereRepository matiereRepository;
    private final FiliereRepository filiereRepository;

    // ENTITY TO DTO
    private EnseignementDTO convertirEnDTO(Enseignement e) {

        return EnseignementDTO.builder()
                .id(e.getId())

                .professeurId(e.getProfesseur().getId())
                .professeurNom(e.getProfesseur().getNom())
                .professeurPrenom(e.getProfesseur().getPrenom())

                .matiereId(e.getMatiere().getId())
                .matiereNom(e.getMatiere().getNom())

                .filiereId(e.getFiliere().getId())
                .filiereNom(e.getFiliere().getNom())

                .heuresCours(e.getHeuresCours())
                .heuresTP(e.getHeuresTP())

                .build();
    }

    // CREATE
    public EnseignementDTO ajouterEnseignement(
            Long professeurId,
            Long matiereId,
            Long filiereId,
            Float heuresCours,
            Float heuresTP) {

        if (heuresCours == null || heuresTP == null
                || heuresCours < 0 || heuresTP < 0) {
            throw new IllegalArgumentException(
                    "Les heures doivent être positives ou nulles"
            );
        }

        Professeur professeur = professeurRepository.findById(professeurId)
                .orElseThrow(() ->
                        new RuntimeException("Professeur introuvable"));

        Matiere matiere = matiereRepository.findById(matiereId)
                .orElseThrow(() ->
                        new RuntimeException("Matiere introuvable"));

        Filiere filiere = filiereRepository.findById(filiereId)
                .orElseThrow(() ->
                        new RuntimeException("Filiere introuvable"));

        boolean existe = enseignementRepository
                .existsByProfesseurAndMatiereAndFiliere(
                        professeur, matiere, filiere
                );

        if (existe) {
            throw new RuntimeException(
                    "Cet enseignement existe déjà pour ce professeur"
            );
        }

        Enseignement enseignement = Enseignement.builder()
                .professeur(professeur)
                .matiere(matiere)
                .filiere(filiere)
                .heuresCours(heuresCours)
                .heuresTP(heuresTP)
                .build();

        Enseignement saved = enseignementRepository.save(enseignement);

        return convertirEnDTO(saved);
    }

    // GET ALL
    @Transactional(readOnly = true)
    public List<EnseignementDTO> getAllEnseignements() {

        return enseignementRepository.findAll()
                .stream()
                .map(this::convertirEnDTO)
                .toList();
    }

    // GET BY ID
    @Transactional(readOnly = true)
    public EnseignementDTO getEnseignementById(Long id) {

        Enseignement enseignement = enseignementRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Enseignement introuvable"));

        return convertirEnDTO(enseignement);
    }

    // GET BY PROFESSOR
    @Transactional(readOnly = true)
    public List<EnseignementDTO> getByProfesseur(Long professeurId) {

        Professeur professeur = professeurRepository.findById(professeurId)
                .orElseThrow(() ->
                        new RuntimeException("Professeur introuvable"));

        return enseignementRepository.findByProfesseur(professeur)
                .stream()
                .map(this::convertirEnDTO)
                .toList();
    }

    // GET BY FILIERE
    @Transactional(readOnly = true)
    public List<EnseignementDTO> getByFiliere(Long filiereId) {

        Filiere filiere = filiereRepository.findById(filiereId)
                .orElseThrow(() ->
                        new RuntimeException("Filiere introuvable"));

        return enseignementRepository.findByFiliere(filiere)
                .stream()
                .map(this::convertirEnDTO)
                .toList();
    }

    // UPDATE HOURS
    public EnseignementDTO modifierHeures(
            Long id,
            Float heuresCours,
            Float heuresTP) {

        if (heuresCours == null || heuresTP == null
                || heuresCours < 0 || heuresTP < 0) {
            throw new IllegalArgumentException(
                    "Les heures doivent être positives ou nulles"
            );
        }

        Enseignement enseignement = enseignementRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Enseignement introuvable"));

        enseignement.setHeuresCours(heuresCours);
        enseignement.setHeuresTP(heuresTP);

        Enseignement updated = enseignementRepository.save(enseignement);

        return convertirEnDTO(updated);
    }

    // DELETE
    public void supprimerEnseignement(Long id) {

        Enseignement enseignement = enseignementRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Enseignement introuvable"));

        enseignementRepository.delete(enseignement);
    }
}
