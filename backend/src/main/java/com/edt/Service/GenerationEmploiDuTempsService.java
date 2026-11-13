
package com.edt.Service;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.time.Duration;
import java.util.Comparator;

import com.edt.entity.*;
import com.edt.entity.enums.StatutSeance;
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

@Service
@RequiredArgsConstructor
@Transactional
public class GenerationEmploiDuTempsService {
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

        // Créneaux déjà occupés par un professeur
        private final Set<String> professeurs = new HashSet<>();

        // Créneaux déjà occupés par un groupe
        private final Set<String> groupes = new HashSet<>();

        // Créneaux déjà occupés par une salle
        private final Set<String> salles = new HashSet<>();
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

    private void verifierDonneesGeneration(Long filiereId) {

        // 1. Vérifier que la filière existe
        var filiere = filiereRepository.findById(filiereId)
                .orElseThrow(() ->
                        new RuntimeException("Filière introuvable"));

        // 2. Récupérer les groupes de cette filière
        var groupes = groupeRepository.findByFiliereId(filiere.getId());

        if (groupes.isEmpty()) {
            throw new RuntimeException(
                    "Aucun groupe trouvé pour cette filière");
        }

        // 3. Récupérer les enseignements de cette filière
        var enseignements =
                enseignementRepository.findByFiliere(filiere);

        if (enseignements.isEmpty()) {
            throw new RuntimeException(
                    "Aucun enseignement trouvé pour cette filière");
        }

        // 4. Vérifier les créneaux horaires
        var creneaux = creneauHoraireRepository.findAll();

        if (creneaux.isEmpty()) {
            throw new RuntimeException(
                    "Aucun créneau horaire enregistré");
        }

        // 5. Vérifier les salles
        var salles = salleRepository.findAll();

        if (salles.isEmpty()) {
            throw new RuntimeException(
                    "Aucune salle enregistrée");
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
    private List<SeanceAPlacer> preparerSeances(Long filiereId) {

        var filiere = filiereRepository.findById(filiereId)
                .orElseThrow(() ->
                        new RuntimeException("Filière introuvable"));

        List<Groupe> groupes =
                groupeRepository.findByFiliereId(filiereId);

        List<Enseignement> enseignements =
                enseignementRepository.findByFiliere(filiere);

        List<SeanceAPlacer> seances = new ArrayList<>();

        for (Groupe groupe : groupes) {

            for (Enseignement enseignement : enseignements) {

                // Nombre de séances COURS
                int nombreCours = calculerNombreSeances(
                        enseignement.getHeuresCours(),
                        1.5f,
                        "COURS"
                );

                // Nombre de séances TP
                int nombreTP = calculerNombreSeances(
                        enseignement.getHeuresTP(),
                        3f,
                        "TP"
                );

                // Ajouter les séances COURS
                for (int i = 0; i < nombreCours; i++) {
                    seances.add(new SeanceAPlacer(
                            enseignement,
                            groupe,
                            TypeSeance.COURS
                    ));
                }

                // Ajouter les séances TP
                for (int i = 0; i < nombreTP; i++) {
                    seances.add(new SeanceAPlacer(
                            enseignement,
                            groupe,
                            TypeSeance.TP
                    ));
                }
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
                    .filter(salle ->
                            "TP".equalsIgnoreCase(salle.getType()))
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

        // Parcourir les créneaux disponibles
        for (CreneauHoraire creneau : creneaux) {

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
                                    professeurId, creneau.getId());

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

                return true;
            }
        }

        // Aucun créneau compatible trouvé
        return false;
    }

    private boolean placerTP(
            SeanceAPlacer aPlacer,
            List<CreneauHoraire> creneaux,
            OccupationPlanning occupation,
            EmploiDuTemps emploi) {

        Long professeurId =
                aPlacer.enseignement.getProfesseur().getId();

        Long groupeId = aPlacer.groupe.getId();

        // 1. Chercher les paires de créneaux compatibles
        List<List<CreneauHoraire>> paires =
                trouverPairesCreneauxTP(professeurId, creneaux);

        // 2. Chercher une paire libre et une salle compatible
        for (List<CreneauHoraire> paire : paires) {

            CreneauHoraire premier = paire.get(0);
            CreneauHoraire deuxieme = paire.get(1);

            // Les deux créneaux doivent être libres
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

                // 3. Créer la première partie du TP
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

                // 4. Créer la deuxième partie du TP
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

                // 5. Enregistrer les deux parties
                seanceRepository.save(seance1);
                seanceRepository.save(seance2);

                // 6. Réserver les deux créneaux
                reserverCreneau(
                        professeurId, groupeId, salle.getId(),
                        premier.getId(), occupation
                );

                reserverCreneau(
                        professeurId, groupeId, salle.getId(),
                        deuxieme.getId(), occupation
                );

                return true;
            }
        }

        // Aucune paire compatible trouvée
        return false;
    }

    public EmploiDuTemps genererEmploiDuTemps(
            Long filiereId,
            String semestre,
            Long administrateurId) {

        // 1. Vérifier les données nécessaires
        verifierDonneesGeneration(filiereId);

        if (semestre == null || semestre.isBlank()) {
            throw new RuntimeException("Le semestre est obligatoire");
        }

        // 2. Récupérer l'administrateur
        Administrateur administrateur =
                administrateurRepository.findById(administrateurId)
                        .orElseThrow(() ->
                                new RuntimeException("Administrateur introuvable"));

        // 3. Préparer les séances
        List<SeanceAPlacer> seances = preparerSeances(filiereId);

        if (seances.isEmpty()) {
            throw new RuntimeException("Aucune séance à générer");
        }

        // 4. Récupérer les créneaux
        List<CreneauHoraire> creneaux =
                creneauHoraireRepository.findAll();

        // 5. Créer l'emploi du temps
        EmploiDuTemps emploi = new EmploiDuTemps();

        emploi.setSemestre(semestre);
        emploi.setPublie(false);
        emploi.setAdministrateur(administrateur);

        emploi = emploiDuTempsRepository.save(emploi);

        // 6. Suivre les occupations
        OccupationPlanning occupation = new OccupationPlanning();

        // 7. Placer les TP avant les COURS
        seances.sort(
                Comparator.comparingInt(
                        s -> s.type == TypeSeance.TP ? 0 : 1
                )
        );

        // 8. Générer toutes les séances
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

            // 9. Vérifier que la séance a été placée
            if (!placee) {
                throw new RuntimeException(
                        "Impossible de placer " + seance.type +
                                " pour la matière " +
                                seance.enseignement.getMatiere().getNom() +
                                " et le groupe " +
                                seance.groupe.getNom()
                );
            }
        }

        return emploi;
    }





}
