
package com.edt.Service;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.time.Duration;
import java.util.Comparator;

import com.edt.dto.SeanceResponse;
import com.edt.entity.*;
import com.edt.entity.enums.StatutSeance;
import com.edt.entity.enums.TypeSalle;
import com.edt.entity.enums.TypeSeance;
import com.edt.repository.EnseignementRepository;
import com.edt.repository.GroupeRepository;
import com.edt.repository.SalleRepository;
import com.edt.repository.CreneauHoraireRepository;
import com.edt.repository.DisponibiliteRepository;
import com.edt.repository.EmploiDuTempsRepository;
import com.edt.repository.SeanceRepository;
import com.edt.repository.FiliereRepository;
import com.edt.repository.AdministrateurRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.HashMap;
import java.util.Map;
@Service
@RequiredArgsConstructor
@Transactional
public class GenerationEmploiDuTempsService {
    private final EnseignementService enseignementService;
    private static class SeanceAPlacer {

        private Enseignement enseignement;
        private Groupe groupe;
        private TypeSeance type;

        public SeanceAPlacer(
                Enseignement enseignement,
                Groupe groupe,
                TypeSeance type) {

            this.enseignement = enseignement;
            this.groupe = groupe;
            this.type = type;
        }
    }
    private static class OccupationPlanning {

        private final Set<String> professeurs = new HashSet<>();
        private final Set<String> groupes = new HashSet<>();
        private final Set<String> salles = new HashSet<>();

        // Total occupied 90-minute slots per group and day
        private final Map<String, Integer> chargeGroupeParJour = new HashMap<>();

        // Number of TP blocks per group and day
        private final Map<String, Integer> tpParGroupeParJour = new HashMap<>();

        // Subjects already scheduled per group and day
        private final Set<String> matieresTPParJour = new HashSet<>();
        private final Set<String> matieresCoursParJour = new HashSet<>();
    }
    // Identifies a group on a specific day
    private String cleGroupeJour(Long groupeId, CreneauHoraire creneau) {
        return groupeId + "-" + creneau.getJour().name();
    }

    // Identifies a subject for a group on a specific day
    private String cleMatiereJour(
            Long groupeId,
            Long matiereId,
            CreneauHoraire creneau
    ) {
        return groupeId + "-" + matiereId + "-" + creneau.getJour().name();
    }

    // Gets the number of occupied slots for a group on a day
    private int getChargeJour(
            OccupationPlanning occupation,
            Long groupeId,
            CreneauHoraire creneau
    ) {
        String key = cleGroupeJour(groupeId, creneau);

        return occupation.chargeGroupeParJour.getOrDefault(key, 0);
    }

    // Gets the number of TP blocks for a group on a day
    private int getNombreTPJour(
            OccupationPlanning occupation,
            Long groupeId,
            CreneauHoraire creneau
    ) {
        String key = cleGroupeJour(groupeId, creneau);

        return occupation.tpParGroupeParJour.getOrDefault(key, 0);
    }
    // Records a scheduled course session
    private void enregistrerCours(
            OccupationPlanning occupation,
            Long groupeId,
            Long matiereId,
            CreneauHoraire creneau
    ) {
        String jourKey = cleGroupeJour(groupeId, creneau);
        String matiereKey = cleMatiereJour(groupeId, matiereId, creneau);

        occupation.chargeGroupeParJour.merge(jourKey, 1, Integer::sum);
        occupation.matieresCoursParJour.add(matiereKey);
    }

    // Records a complete TP block (two consecutive slots)
    private void enregistrerTP(
            OccupationPlanning occupation,
            Long groupeId,
            Long matiereId,
            CreneauHoraire premier,
            CreneauHoraire deuxieme
    ) {
        String jourKey = cleGroupeJour(groupeId, premier);
        String matiereKey = cleMatiereJour(groupeId, matiereId, premier);

        // A TP block occupies two 90-minute slots
        occupation.chargeGroupeParJour.merge(jourKey, 2, Integer::sum);

        // Count one TP block, not two sessions
        occupation.tpParGroupeParJour.merge(jourKey, 1, Integer::sum);

        occupation.matieresTPParJour.add(matiereKey);
    }

    private final EnseignementRepository enseignementRepository;
    private final GroupeRepository groupeRepository;
    private final SalleRepository salleRepository;
    private final CreneauHoraireRepository creneauHoraireRepository;
    private final DisponibiliteRepository disponibiliteRepository;
    private final EmploiDuTempsRepository emploiDuTempsRepository;
    private final SeanceRepository seanceRepository;

    private final FiliereRepository filiereRepository;
    private final AdministrateurRepository administrateurRepository;


    private void verifierDonneesGeneration(
            Long filiereId,
            String semestre
    ) {

        // 1. Verify that the filiere exists.
        var filiere = filiereRepository.findById(filiereId)
                .orElseThrow(() ->
                        new RuntimeException("Filière introuvable"));

        // 2. Verify that groups exist for this semester.
        var groupes = groupeRepository.findByFiliereIdAndSemestre(
                filiere.getId(),
                semestre
        );

        if (groupes.isEmpty()) {
            throw new RuntimeException(
                    "Aucun groupe trouvé pour cette filière et ce semestre"
            );
        }

        // 3. Verify that time slots exist.
        var creneaux = creneauHoraireRepository.findAll();

        if (creneaux.isEmpty()) {
            throw new RuntimeException(
                    "Aucun créneau horaire enregistré"
            );
        }

        // 4. Verify that rooms exist.
        var salles = salleRepository.findAll();

        if (salles.isEmpty()) {
            throw new RuntimeException(
                    "Aucune salle enregistrée"
            );
        }
    }

    private int calculerNombreSeances(Float heures, float dureeSeance,
                                      String typeSeance) {

        if (heures == null || heures < 0) {
            throw new RuntimeException(
                    "Nombre d'heures invalide pour " + typeSeance);
        }

        int nombre = Math.round(heures / dureeSeance);

        // Vérifier que les heures sont divisibles par la durée
        if (Math.abs(heures - (nombre * dureeSeance)) > 0.001f) {
            throw new RuntimeException(
                    "Les heures de " + typeSeance +
                            " doivent être un multiple de " + dureeSeance);
        }

        return nombre;
    }

    private List<SeanceAPlacer> preparerSeances(
            Long filiereId,
            String semestre
    ) {

        // 1. Generate automatic professor assignments.
        List<Enseignement> enseignements =
                enseignementService.genererAffectationsAutomatiques(filiereId, semestre);

        // 2. Prepare timetable sessions.
        List<SeanceAPlacer> seances = new ArrayList<>();
        if (enseignements.isEmpty()) {
            throw new RuntimeException(
                    "Aucune affectation automatique n'a été générée"
            );
        }
        for (Enseignement enseignement : enseignements) {

            Groupe groupe = enseignement.getGroupe();

            if (groupe == null) {
                throw new RuntimeException(
                        "Aucun groupe associé à l'affectation "
                                + enseignement.getId()
                );
            }

            // Number of COURSE sessions (90 minutes each).
            int nombreCours = calculerNombreSeances(
                    enseignement.getHeuresCours(),
                    1.5f,
                    "COURS"
            );

            // Number of TP sessions (two 90-minute slots each).
            int nombreTP = calculerNombreSeances(
                    enseignement.getHeuresTP(),
                    3f,
                    "TP"
            );

            // Add COURSE sessions.
            for (int i = 0; i < nombreCours; i++) {

                seances.add(new SeanceAPlacer(
                        enseignement,
                        groupe,
                        TypeSeance.COURS
                ));
            }

            // Add TP sessions.
            for (int i = 0; i < nombreTP; i++) {

                seances.add(new SeanceAPlacer(
                        enseignement,
                        groupe,
                        TypeSeance.TP
                ));
            }
        }

        return seances;
    }

    private List<List<CreneauHoraire>> trouverPairesCreneauxTP(
            Long professeurId,
            List<CreneauHoraire> creneaux) {

        List<List<CreneauHoraire>> paires = new ArrayList<>();

        // Trier les créneaux par jour puis par heure de début
        creneaux.sort(
                Comparator.comparing(CreneauHoraire::getJour)
                        .thenComparing(CreneauHoraire::getHeureDebut)
        );

        for (int i = 0; i < creneaux.size(); i++) {

            CreneauHoraire premier = creneaux.get(i);

            // Le premier créneau doit durer 1 h 30
            long duree1 = Duration.between(
                    premier.getHeureDebut(),
                    premier.getHeureFin()
            ).toMinutes();

            if (duree1 != 90) {
                continue;
            }

            for (int j = i + 1; j < creneaux.size(); j++) {

                CreneauHoraire deuxieme = creneaux.get(j);

                // Même jour obligatoire
                if (premier.getJour() != deuxieme.getJour()) {
                    continue;
                }

                // Le deuxième doit commencer 5 minutes après le premier
                if (!premier.getHeureFin().plusMinutes(5)
                        .equals(deuxieme.getHeureDebut())) {
                    continue;
                }

                // Le deuxième doit également durer 1 h 30
                long duree2 = Duration.between(
                        deuxieme.getHeureDebut(),
                        deuxieme.getHeureFin()
                ).toMinutes();

                if (duree2 != 90) {
                    continue;
                }

                // Vérifier la disponibilité du professeur
                boolean disponible1 =
                        disponibiliteRepository
                                .existsByProfesseurIdAndCreneauId(
                                        professeurId, premier.getId());

                boolean disponible2 =
                        disponibiliteRepository
                                .existsByProfesseurIdAndCreneauId(
                                        professeurId, deuxieme.getId());

                if (disponible1 && disponible2) {

                    List<CreneauHoraire> paire = new ArrayList<>();

                    paire.add(premier);
                    paire.add(deuxieme);

                    paires.add(paire);
                }
            }
        }

        return paires;
    }

    private List<Salle> trouverSallesCompatibles(
            Groupe groupe,
            TypeSeance type) {

        if (groupe.getEffectif() == null || groupe.getEffectif() <= 0) {
            throw new RuntimeException(
                    "Effectif invalide pour le groupe : " + groupe.getNom()
            );
        }

        List<Salle> salles = salleRepository
                .findByCapaciteGreaterThanEqual(groupe.getEffectif());

        // Pour un TP, garder uniquement les salles de type TP
        if (type == TypeSeance.TP) {

            salles = salles.stream()
                    .filter(salle -> salle.getType() == TypeSalle.TP)
                    .toList();
        }

        // Choisir d'abord les salles avec la plus petite capacité suffisante
        return salles.stream()
                .sorted(Comparator.comparing(Salle::getCapacite))
                .toList();
    }
    private boolean estCreneauLibre(
            Long professeurId,
            Long groupeId,
            Long salleId,
            Long creneauId,
            OccupationPlanning occupation) {

        String cleProfesseur = professeurId + "-" + creneauId;
        String cleGroupe = groupeId + "-" + creneauId;
        String cleSalle = salleId + "-" + creneauId;

        // Vérifier les trois conflits
        if (occupation.professeurs.contains(cleProfesseur)) {
            return false;
        }

        if (occupation.groupes.contains(cleGroupe)) {
            return false;
        }

        if (occupation.salles.contains(cleSalle)) {
            return false;
        }

        return true;
    }
    private void reserverCreneau(
            Long professeurId,
            Long groupeId,
            Long salleId,
            Long creneauId,
            OccupationPlanning occupation) {

        occupation.professeurs.add(professeurId + "-" + creneauId);
        occupation.groupes.add(groupeId + "-" + creneauId);
        occupation.salles.add(salleId + "-" + creneauId);
    }


    private boolean placerCours(
            SeanceAPlacer aPlacer,
            List<CreneauHoraire> creneaux,
            OccupationPlanning occupation,
            EmploiDuTemps emploi) {

        Long professeurId =
                aPlacer.enseignement.getProfesseur().getId();

        Long groupeId = aPlacer.groupe.getId();

        Long matiereId =
                aPlacer.enseignement.getMatiere().getId();

        // 1. Trier les créneaux selon la répartition de la semaine
        List<CreneauHoraire> creneauxTries =
                new ArrayList<>(creneaux);

        creneauxTries.sort(
                Comparator
                        // Éviter le jour où cette matière a déjà un TP
                        .comparingInt((CreneauHoraire creneau) ->
                                existeTPMatiereCeJour(
                                        occupation,
                                        groupeId,
                                        matiereId,
                                        creneau
                                ) ? 1 : 0
                        )
                        // Préférer les jours les moins chargés
                        .thenComparingInt(creneau ->
                                getChargeJour(
                                        occupation,
                                        groupeId,
                                        creneau
                                )
                        )
                        // En cas d'égalité, répartir selon l'heure
                        .thenComparing(CreneauHoraire::getHeureDebut)
        );

        // 2. Parcourir les créneaux dans cet ordre
        for (CreneauHoraire creneau : creneauxTries) {

            // Un COURS doit durer exactement 1 h 30
            long duree = Duration.between(
                    creneau.getHeureDebut(),
                    creneau.getHeureFin()
            ).toMinutes();

            if (duree != 90) {
                continue;
            }

            // Le professeur doit être disponible
            boolean disponible =
                    disponibiliteRepository
                            .existsByProfesseurIdAndCreneauId(
                                    professeurId,
                                    creneau.getId()
                            );

            if (!disponible) {
                continue;
            }

            // Chercher une salle de capacité suffisante
            List<Salle> salles = trouverSallesCompatibles(
                    aPlacer.groupe,
                    TypeSeance.COURS
            );

            for (Salle salle : salles) {

                // Vérifier les conflits
                boolean libre = estCreneauLibre(
                        professeurId,
                        groupeId,
                        salle.getId(),
                        creneau.getId(),
                        occupation
                );

                if (!libre) {
                    continue;
                }

                // Créer la séance
                Seance seance = new Seance();

                seance.setDate(null);
                seance.setStatut(StatutSeance.PLANIFIEE);
                seance.setType(TypeSeance.COURS);
                seance.setProfesseur(aPlacer.enseignement.getProfesseur());
                seance.setGroupe(aPlacer.groupe);
                seance.setMatiere(aPlacer.enseignement.getMatiere());
                seance.setSalle(salle);
                seance.setCreneau(creneau);
                seance.setEmploiDuTemps(emploi);

                // Enregistrer la séance
                seanceRepository.save(seance);

                // Réserver le créneau
                reserverCreneau(
                        professeurId,
                        groupeId,
                        salle.getId(),
                        creneau.getId(),
                        occupation
                );

                // Mettre à jour les statistiques
                enregistrerCours(
                        occupation,
                        groupeId,
                        matiereId,
                        creneau
                );

                return true;
            }
        }

        // Aucun créneau compatible trouvé
        return false;
    }
    private boolean existeTPMatiereCeJour(
            OccupationPlanning occupation,
            Long groupeId,
            Long matiereId,
            CreneauHoraire creneau
    ) {
        String key = cleMatiereJour(groupeId, matiereId, creneau);

        return occupation.matieresTPParJour.contains(key);
    }

    private boolean placerTP(
            SeanceAPlacer aPlacer,
            List<CreneauHoraire> creneaux,
            OccupationPlanning occupation,
            EmploiDuTemps emploi) {

        Long professeurId =
                aPlacer.enseignement.getProfesseur().getId();

        Long groupeId = aPlacer.groupe.getId();

        Long matiereId =
                aPlacer.enseignement.getMatiere().getId();

        // 1. Chercher les paires de créneaux compatibles
        List<List<CreneauHoraire>> paires =
                trouverPairesCreneauxTP(professeurId, creneaux);

        // 2. Trier les paires selon les préférences de répartition
        paires.sort(
                Comparator
                        // Éviter le même jour que le cours de cette matière
                        .comparingInt((List<CreneauHoraire> paire) ->
                                existeCoursMatiereCeJour(
                                        occupation,
                                        groupeId,
                                        matiereId,
                                        paire.get(0)
                                ) ? 1 : 0
                        )
                        // Préférer les jours les moins chargés
                        .thenComparingInt(paire ->
                                getChargeJour(
                                        occupation,
                                        groupeId,
                                        paire.get(0)
                                )
                        )
                        // Puis préférer les jours avec moins de TP
                        .thenComparingInt(paire ->
                                getNombreTPJour(
                                        occupation,
                                        groupeId,
                                        paire.get(0)
                                )
                        )
        );

        // 3. Chercher une paire libre et une salle compatible
        for (List<CreneauHoraire> paire : paires) {

            CreneauHoraire premier = paire.get(0);
            CreneauHoraire deuxieme = paire.get(1);

            // Maximum 2 blocs TP par groupe et par jour
            if (getNombreTPJour(
                    occupation,
                    groupeId,
                    premier
            ) >= 2) {
                continue;
            }

            // Chercher les salles compatibles
            List<Salle> salles = trouverSallesCompatibles(
                    aPlacer.groupe,
                    TypeSeance.TP
            );

            for (Salle salle : salles) {

                boolean premierLibre = estCreneauLibre(
                        professeurId,
                        groupeId,
                        salle.getId(),
                        premier.getId(),
                        occupation
                );

                boolean deuxiemeLibre = estCreneauLibre(
                        professeurId,
                        groupeId,
                        salle.getId(),
                        deuxieme.getId(),
                        occupation
                );

                if (!premierLibre || !deuxiemeLibre) {
                    continue;
                }

                // 4. Créer la première partie du TP
                Seance seance1 = new Seance();

                seance1.setDate(null);
                seance1.setStatut(StatutSeance.PLANIFIEE);
                seance1.setType(TypeSeance.TP);
                seance1.setProfesseur(aPlacer.enseignement.getProfesseur());
                seance1.setGroupe(aPlacer.groupe);
                seance1.setMatiere(aPlacer.enseignement.getMatiere());
                seance1.setSalle(salle);
                seance1.setCreneau(premier);
                seance1.setEmploiDuTemps(emploi);

                // 5. Créer la deuxième partie du TP
                Seance seance2 = new Seance();

                seance2.setDate(null);
                seance2.setStatut(StatutSeance.PLANIFIEE);
                seance2.setType(TypeSeance.TP);
                seance2.setProfesseur(aPlacer.enseignement.getProfesseur());
                seance2.setGroupe(aPlacer.groupe);
                seance2.setMatiere(aPlacer.enseignement.getMatiere());
                seance2.setSalle(salle);
                seance2.setCreneau(deuxieme);
                seance2.setEmploiDuTemps(emploi);

                // 6. Enregistrer les deux parties
                seanceRepository.save(seance1);
                seanceRepository.save(seance2);

                // 7. Réserver les deux créneaux
                reserverCreneau(
                        professeurId,
                        groupeId,
                        salle.getId(),
                        premier.getId(),
                        occupation
                );

                reserverCreneau(
                        professeurId,
                        groupeId,
                        salle.getId(),
                        deuxieme.getId(),
                        occupation
                );

                // 8. Mettre à jour les statistiques du groupe
                enregistrerTP(
                        occupation,
                        groupeId,
                        matiereId,
                        premier,
                        deuxieme
                );

                return true;
            }
        }

        // Aucune paire compatible trouvée
        return false;
    }

    private boolean existeCoursMatiereCeJour(
            OccupationPlanning occupation,
            Long groupeId,
            Long matiereId,
            CreneauHoraire creneau
    ) {
        String key = cleMatiereJour(groupeId, matiereId, creneau);

        return occupation.matieresCoursParJour.contains(key);
    }
    @Transactional
    public EmploiDuTemps genererEmploiDuTemps(
            Long filiereId,
            String semestre,
            Long administrateurId) {

        // 1. Validate the required data.
        verifierDonneesGeneration(filiereId, semestre);

        if (semestre == null || semestre.isBlank()) {
            throw new RuntimeException("Le semestre est obligatoire");
        }

// 2. Validate the required data for this semester.
        verifierDonneesGeneration(filiereId, semestre);

        // 2. Find previous timetables for this filiere and semester.
        List<EmploiDuTemps> anciensEmplois =
                seanceRepository.findTimetablesByFiliereAndSemestre(
                        filiereId,
                        semestre
                );

        // 3. Retrieve the administrator.
        Administrateur administrateur =
                administrateurRepository.findById(administrateurId)
                        .orElseThrow(() ->
                                new RuntimeException("Administrateur introuvable"));

        // 4. Prepare sessions using automatic professor assignments.
        List<SeanceAPlacer> seances = preparerSeances(filiereId, semestre);

        if (seances.isEmpty()) {
            throw new RuntimeException("Aucune séance à générer");
        }

        // 5. Retrieve available time slots.
        List<CreneauHoraire> creneaux =
                creneauHoraireRepository.findAll();

        // 6. Create the new timetable.
        EmploiDuTemps emploi = new EmploiDuTemps();

        emploi.setSemestre(semestre);
        emploi.setPublie(false);
        emploi.setAdministrateur(administrateur);

        emploi = emploiDuTempsRepository.save(emploi);

        // 7. Track occupied slots.
        OccupationPlanning occupation = new OccupationPlanning();

        // 8. Alternate courses and TPs instead of placing all TPs first.

        List<SeanceAPlacer> cours = new ArrayList<>();
        List<SeanceAPlacer> tp = new ArrayList<>();
        List<SeanceAPlacer> seancesOrdonnees = new ArrayList<>();

// Separate courses and TPs
        for (SeanceAPlacer s : seances) {
            if (s.type == TypeSeance.TP) {
                tp.add(s);
            } else {
                cours.add(s);
            }
        }

// Alternate between courses and TPs
        int max = Math.max(cours.size(), tp.size());

        for (int i = 0; i < max; i++) {

            if (i < cours.size()) {
                seancesOrdonnees.add(cours.get(i));
            }

            if (i < tp.size()) {
                seancesOrdonnees.add(tp.get(i));
            }
        }

// Use the new ordering
        seances = seancesOrdonnees;

        // 9. Generate all sessions.
        for (SeanceAPlacer seance : seances) {

            boolean placee;

            if (seance.type == TypeSeance.TP) {

                placee = placerTP(
                        seance,
                        creneaux,
                        occupation,
                        emploi
                );

            } else {

                placee = placerCours(
                        seance,
                        creneaux,
                        occupation,
                        emploi
                );
            }

            if (!placee) {
                throw new RuntimeException(
                        "Impossible de placer " + seance.type
                                + " pour la matière "
                                + seance.enseignement.getMatiere().getNom()
                                + " et le groupe "
                                + seance.groupe.getNom()
                );
            }
        }

        // 10. Replace old timetables only after successful generation.
        if (!anciensEmplois.isEmpty()) {

            emploiDuTempsRepository.deleteAll(anciensEmplois);
            emploiDuTempsRepository.flush();
        }

        return emploi;
    }

    @GetMapping("/emploi/{emploiId}/seances")
    @Transactional(readOnly = true)
    public List<SeanceResponse> getSeances(@PathVariable Long emploiId) {
        return seanceRepository.findByEmploiDuTempsId(emploiId)
                .stream()
                .map(s -> new SeanceResponse(
                        s.getId(),
                        s.getDate() != null ? s.getDate().toString() : null,
                        String.valueOf(s.getCreneau().getJour()),
                        String.valueOf(s.getCreneau().getHeureDebut()),
                        String.valueOf(s.getCreneau().getHeureFin()),
                        s.getType() != null ? s.getType().name() : null,
                        s.getStatut() != null ? s.getStatut().name() : null,
                        s.getMatiere().getNom(),
                        s.getProfesseur().getNom(),
                        s.getSalle().getNom(),
                        s.getGroupe().getNom()))
                .toList();
    }





}
