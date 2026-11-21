
package com.edt.service;

import com.edt.entity.*;
import com.edt.dto.EnseignementDTO;
import com.edt.repository.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class EnseignementService {

    private final EnseignementRepository enseignementRepository;
    private final ProfesseurRepository professeurRepository;
    private final MatiereRepository matiereRepository;
    private final FiliereRepository filiereRepository;

    // Repositories for automatic allocation
    private final FiliereMatiereRepository filiereMatiereRepository;
    private final PreferenceMatiereRepository preferenceMatiereRepository;
    private final GroupeRepository groupeRepository;

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

    // =====================================================
    // AUTOMATIC PROFESSOR ALLOCATION
    // =====================================================

    public List<Enseignement> genererAffectationsAutomatiques(
            Long filiereId,
            String semestre) {

        Filiere filiere = filiereRepository.findById(filiereId)
                .orElseThrow(() ->
                        new RuntimeException("Filiere introuvable"));

        List<Groupe> groupes =
                groupeRepository.findByFiliereIdAndSemestre(filiereId, semestre);

        if (groupes.isEmpty()) {
            throw new RuntimeException(
                    "Aucun groupe trouvé pour cette filière"
            );
        }

        List<FiliereMatiere> matieres =
                filiereMatiereRepository.findByFiliereIdAndSemestre(
                        filiereId, semestre);

        if (matieres.isEmpty()) {
            throw new RuntimeException(
                    "Aucune matière configurée pour cette filière"
            );
        }

        // Store the workload of every eligible professor.
        Map<Long, Professeur> professeurs = new HashMap<>();
        Map<Long, Float> charges = new HashMap<>();

        // Store the preferences for each subject.
        Map<Long, List<Professeur>> professeursParMatiere =
                new HashMap<>();

        for (FiliereMatiere fm : matieres) {

            Long matiereId = fm.getMatiere().getId();

            List<PreferenceMatiere> preferences =
                    preferenceMatiereRepository.findByMatiereId(matiereId);

            List<Professeur> candidats = new ArrayList<>();

            for (PreferenceMatiere preference : preferences) {

                Professeur professeur = preference.getProfesseur();

                if (professeur == null) {
                    continue;
                }

                candidats.add(professeur);

                Long professeurId = professeur.getId();

                if (!professeurs.containsKey(professeurId)) {

                    professeurs.put(professeurId, professeur);

                    float chargeActuelle = 0;

                    List<Enseignement> anciens =
                            enseignementRepository.findByProfesseur(professeur);

                    for (Enseignement ancien : anciens) {

                        // Ignore previous automatic assignments
                        // of the filière being regenerated.
                        if (ancien.isGenereAutomatiquement()
                                && ancien.getFiliere().getId().equals(filiereId)
                                && ancien.getGroupe() != null
                                && ancien.getGroupe().getSemestres().contains(semestre)) {
                            continue;
                        }

                        chargeActuelle += ancien.getHeuresCours() == null
                                ? 0 : ancien.getHeuresCours();

                        chargeActuelle += ancien.getHeuresTP() == null
                                ? 0 : ancien.getHeuresTP();
                    }

                    charges.put(professeurId, chargeActuelle);
                }
            }

            if (candidats.isEmpty()
                    && ((fm.getHeuresCours() != null && fm.getHeuresCours() > 0)
                    || (fm.getHeuresTP() != null && fm.getHeuresTP() > 0))) {

                throw new RuntimeException(
                        "Aucun professeur n'a choisi la matière : "
                                + fm.getMatiere().getNom()
                );
            }

            professeursParMatiere.put(matiereId, candidats);
        }

        // Prepare all new assignments before replacing old ones.
        List<Enseignement> nouvellesAffectations = new ArrayList<>();

        for (Groupe groupe : groupes) {

            for (FiliereMatiere fm : matieres) {

                Matiere matiere = fm.getMatiere();

                float heuresCours = fm.getHeuresCours() == null
                        ? 0 : fm.getHeuresCours();

                float heuresTP = fm.getHeuresTP() == null
                        ? 0 : fm.getHeuresTP();

                if (heuresCours < 0 || heuresTP < 0) {
                    throw new RuntimeException(
                            "Les heures de la matière "
                                    + matiere.getNom()
                                    + " ne peuvent pas être négatives"
                    );
                }

                List<Professeur> candidats =
                        professeursParMatiere.get(matiere.getId());

                if (heuresCours == 0 && heuresTP == 0) {
                    continue;
                }

                if (candidats == null || candidats.isEmpty()) {
                    throw new RuntimeException(
                            "Aucun professeur disponible pour "
                                    + matiere.getNom()
                    );
                }

                // Professor -> [course hours, TP hours]
                Map<Long, float[]> repartition = new LinkedHashMap<>();

                // TP: all TP hours must belong to ONE professor.
                if (heuresTP > 0) {

                    Professeur professeurTP =
                            choisirProfesseur(candidats, charges);

                    float[] heures = repartition.computeIfAbsent(
                            professeurTP.getId(),
                            id -> new float[]{0, 0}
                    );

                    heures[1] = heuresTP;

                    charges.put(
                            professeurTP.getId(),
                            charges.get(professeurTP.getId()) + heuresTP
                    );
                }

                // Course: distribute in 1.5-hour units.
                if (heuresCours > 0) {

                    int nombreSeancesCours =
                            calculerUnites(heuresCours, 1.5f, "cours");

                    for (int i = 0; i < nombreSeancesCours; i++) {

                        Professeur professeurCours =
                                choisirProfesseur(candidats, charges);

                        float[] heures = repartition.computeIfAbsent(
                                professeurCours.getId(),
                                id -> new float[]{0, 0}
                        );

                        heures[0] += 1.5f;

                        charges.put(
                                professeurCours.getId(),
                                charges.get(professeurCours.getId()) + 1.5f
                        );
                    }
                }

                // Create one assignment per professor and group.
                for (Map.Entry<Long, float[]> entry : repartition.entrySet()) {

                    Professeur professeur =
                            professeurs.get(entry.getKey());

                    float[] heures = entry.getValue();

                    Enseignement enseignement = Enseignement.builder()
                            .professeur(professeur)
                            .matiere(matiere)
                            .filiere(filiere)
                            .groupe(groupe)
                            .heuresCours(heures[0])
                            .heuresTP(heures[1])
                            .genereAutomatiquement(true)
                            .build();

                    nouvellesAffectations.add(enseignement);
                }
            }
        }

        // Replace only automatic assignments for this filière and semester.

        enseignementRepository.deleteAutomaticAssignmentsByFiliereAndSemester(
                filiereId,
                semestre
        );

        return enseignementRepository.saveAll(nouvellesAffectations);
    }

    // Choose the professor with the lowest current workload.
    private Professeur choisirProfesseur(
            List<Professeur> candidats,
            Map<Long, Float> charges) {

        return candidats.stream()
                .min(
                        Comparator
                                .comparing(
                                        (Professeur p) ->
                                                charges.getOrDefault(p.getId(), 0f)
                                )
                                .thenComparing(Professeur::getId)
                )
                .orElseThrow(() ->
                        new RuntimeException("Aucun professeur candidat"));
    }

    // Check that the hours can be scheduled in complete units.
    private int calculerUnites(
            float heures,
            float dureeUnite,
            String type) {

        int nombre = Math.round(heures / dureeUnite);

        if (Math.abs(heures - nombre * dureeUnite) > 0.001f) {
            throw new RuntimeException(
                    "Les heures de " + type
                            + " doivent être un multiple de "
                            + dureeUnite + " heures"
            );
        }

        return nombre;
    }

    // =====================================================
    // EXISTING CRUD METHODS
    // =====================================================

    // CREATE MANUAL ASSIGNMENT
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
                    "Ce professeur est déjà affecté à cette matière dans cette filière"
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

    // GET BY SUBJECT AND FILIERE
    @Transactional(readOnly = true)
    public List<EnseignementDTO> getByMatiereAndFiliere(
            Long matiereId,
            Long filiereId) {

        Matiere matiere = matiereRepository.findById(matiereId)
                .orElseThrow(() ->
                        new RuntimeException("Matiere introuvable"));

        Filiere filiere = filiereRepository.findById(filiereId)
                .orElseThrow(() ->
                        new RuntimeException("Filiere introuvable"));

        return enseignementRepository
                .findByMatiereAndFiliere(matiere, filiere)
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
