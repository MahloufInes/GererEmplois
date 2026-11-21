package com.edt.service;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.time.Duration;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

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

@Service
@RequiredArgsConstructor
@Transactional
public class GenerationEmploiDuTempsService {

    private final EnseignementService enseignementService;

    private static class SeanceAPlacer {
        private Enseignement enseignement;
        private Groupe groupe;
        private TypeSeance type;

        public SeanceAPlacer(Enseignement enseignement, Groupe groupe, TypeSeance type) {
            this.enseignement = enseignement;
            this.groupe = groupe;
            this.type = type;
        }
    }

    private static class OccupationPlanning {
        private final Set<String> professeurs = new HashSet<>();
        private final Set<String> groupes = new HashSet<>();
        private final Set<String> salles = new HashSet<>();
        private final Map<String, Integer> chargeGroupeParJour = new HashMap<>();
        private final Map<String, Integer> tpParGroupeParJour = new HashMap<>();
        private final Set<String> matieresTPParJour = new HashSet<>();
        private final Set<String> matieresCoursParJour = new HashSet<>();
    }

    private String cleGroupeJour(Long groupeId, CreneauHoraire creneau) {
        return groupeId + "-" + creneau.getJour().name();
    }

    private String cleMatiereJour(Long groupeId, Long matiereId, CreneauHoraire creneau) {
        return groupeId + "-" + matiereId + "-" + creneau.getJour().name();
    }

    private int getChargeJour(OccupationPlanning occupation, Long groupeId, CreneauHoraire creneau) {
        String key = cleGroupeJour(groupeId, creneau);
        return occupation.chargeGroupeParJour.getOrDefault(key, 0);
    }

    private int getNombreTPJour(OccupationPlanning occupation, Long groupeId, CreneauHoraire creneau) {
        String key = cleGroupeJour(groupeId, creneau);
        return occupation.tpParGroupeParJour.getOrDefault(key, 0);
    }

    private void enregistrerCours(OccupationPlanning occupation, Long groupeId, Long matiereId, CreneauHoraire creneau) {
        String jourKey = cleGroupeJour(groupeId, creneau);
        String matiereKey = cleMatiereJour(groupeId, matiereId, creneau);

        occupation.chargeGroupeParJour.merge(jourKey, 1, Integer::sum);
        occupation.matieresCoursParJour.add(matiereKey);
    }

    private void enregistrerTP(OccupationPlanning occupation, Long groupeId, Long matiereId,
                               CreneauHoraire premier, CreneauHoraire deuxieme) {
        String jourKey = cleGroupeJour(groupeId, premier);
        String matiereKey = cleMatiereJour(groupeId, matiereId, premier);

        occupation.chargeGroupeParJour.merge(jourKey, 2, Integer::sum);
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

    private void verifierDonneesGeneration(Long filiereId, String semestre) {
        var filiere = filiereRepository.findById(filiereId)
                .orElseThrow(() -> new RuntimeException("Filière introuvable"));

        var groupes = groupeRepository.findByFiliereIdAndSemestre(filiere.getId(), semestre);

        if (groupes.isEmpty()) {
            throw new RuntimeException("Aucun groupe trouvé pour cette filière et ce semestre");
        }

        var creneaux = creneauHoraireRepository.findAll();

        if (creneaux.isEmpty()) {
            throw new RuntimeException("Aucun créneau horaire enregistré");
        }

        var salles = salleRepository.findAll();

        if (salles.isEmpty()) {
            throw new RuntimeException("Aucune salle enregistrée");
        }
    }

    private int calculerNombreSeances(Float heures, float dureeSeance, String typeSeance) {
        if (heures == null || heures < 0) {
            throw new RuntimeException("Nombre d'heures invalide pour " + typeSeance);
        }

        int nombre = Math.round(heures / dureeSeance);

        if (Math.abs(heures - (nombre * dureeSeance)) > 0.001f) {
            throw new RuntimeException("Les heures de " + typeSeance +
                    " doivent être un multiple de " + dureeSeance);
        }

        return nombre;
    }

    private List<SeanceAPlacer> preparerSeances(Long filiereId, String semestre) {
        List<Enseignement> enseignements =
                enseignementService.genererAffectationsAutomatiques(filiereId, semestre);

        List<SeanceAPlacer> seances = new ArrayList<>();

        if (enseignements.isEmpty()) {
            throw new RuntimeException("Aucune affectation automatique n'a été générée");
        }

        for (Enseignement enseignement : enseignements) {
            Groupe groupe = enseignement.getGroupe();

            if (groupe == null) {
                throw new RuntimeException("Aucun groupe associé à l'affectation " + enseignement.getId());
            }

            int nombreCours = calculerNombreSeances(enseignement.getHeuresCours(), 1.5f, "COURS");
            int nombreTP = calculerNombreSeances(enseignement.getHeuresTP(), 3f, "TP");

            for (int i = 0; i < nombreCours; i++) {
                seances.add(new SeanceAPlacer(enseignement, groupe, TypeSeance.COURS));
            }

            for (int i = 0; i < nombreTP; i++) {
                seances.add(new SeanceAPlacer(enseignement, groupe, TypeSeance.TP));
            }
        }

        return seances;
    }

    private List<List<CreneauHoraire>> trouverPairesCreneauxTP(Long professeurId, List<CreneauHoraire> creneaux) {
        List<List<CreneauHoraire>> paires = new ArrayList<>();

        creneaux.sort(Comparator.comparing(CreneauHoraire::getJour)
                .thenComparing(CreneauHoraire::getHeureDebut));

        for (int i = 0; i < creneaux.size(); i++) {
            CreneauHoraire premier = creneaux.get(i);

            long duree1 = Duration.between(premier.getHeureDebut(), premier.getHeureFin()).toMinutes();

            if (duree1 != 90) continue;

            for (int j = i + 1; j < creneaux.size(); j++) {
                CreneauHoraire deuxieme = creneaux.get(j);

                if (premier.getJour() != deuxieme.getJour()) continue;

                if (!premier.getHeureFin().plusMinutes(5).equals(deuxieme.getHeureDebut())) continue;

                long duree2 = Duration.between(deuxieme.getHeureDebut(), deuxieme.getHeureFin()).toMinutes();

                if (duree2 != 90) continue;

                boolean disponible1 = disponibiliteRepository.existsByProfesseurIdAndCreneauId(
                        professeurId, premier.getId());

                boolean disponible2 = disponibiliteRepository.existsByProfesseurIdAndCreneauId(
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

    private List<Salle> trouverSallesCompatibles(Groupe groupe, TypeSeance type) {
        if (groupe.getEffectif() == null || groupe.getEffectif() <= 0) {
            throw new RuntimeException("Effectif invalide pour le groupe : " + groupe.getNom());
        }

        List<Salle> salles = salleRepository.findByCapaciteGreaterThanEqual(groupe.getEffectif());

        if (type == TypeSeance.TP) {
            salles = salles.stream()
                    .filter(salle -> salle.getType() == TypeSalle.TP)
                    .toList();
        }

        return salles.stream()
                .sorted(Comparator.comparing(Salle::getCapacite))
                .toList();
    }

    private boolean estCreneauLibre(Long professeurId, Long groupeId, Long salleId,
                                    Long creneauId, OccupationPlanning occupation) {
        String cleProfesseur = professeurId + "-" + creneauId;
        String cleGroupe = groupeId + "-" + creneauId;
        String cleSalle = salleId + "-" + creneauId;

        return !occupation.professeurs.contains(cleProfesseur)
                && !occupation.groupes.contains(cleGroupe)
                && !occupation.salles.contains(cleSalle);
    }

    private void reserverCreneau(Long professeurId, Long groupeId, Long salleId,
                                 Long creneauId, OccupationPlanning occupation) {
        occupation.professeurs.add(professeurId + "-" + creneauId);
        occupation.groupes.add(groupeId + "-" + creneauId);
        occupation.salles.add(salleId + "-" + creneauId);
    }

    private boolean placerCours(SeanceAPlacer aPlacer, List<CreneauHoraire> creneaux,
                                OccupationPlanning occupation, EmploiDuTemps emploi) {
        Long professeurId = aPlacer.enseignement.getProfesseur().getId();
        Long groupeId = aPlacer.groupe.getId();
        Long matiereId = aPlacer.enseignement.getMatiere().getId();

        List<CreneauHoraire> creneauxTries = new ArrayList<>(creneaux);

        creneauxTries.sort(Comparator
                .comparingInt((CreneauHoraire creneau) ->
                        existeTPMatiereCeJour(occupation, groupeId, matiereId, creneau) ? 1 : 0)
                .thenComparingInt(creneau -> getChargeJour(occupation, groupeId, creneau))
                .thenComparing(CreneauHoraire::getHeureDebut));

        for (CreneauHoraire creneau : creneauxTries) {
            long duree = Duration.between(creneau.getHeureDebut(), creneau.getHeureFin()).toMinutes();

            if (duree != 90) continue;

            boolean disponible = disponibiliteRepository.existsByProfesseurIdAndCreneauId(
                    professeurId, creneau.getId());

            if (!disponible) continue;

            List<Salle> salles = trouverSallesCompatibles(aPlacer.groupe, TypeSeance.COURS);

            for (Salle salle : salles) {
                boolean libre = estCreneauLibre(professeurId, groupeId, salle.getId(),
                        creneau.getId(), occupation);

                if (!libre) continue;

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

                seanceRepository.save(seance);

                reserverCreneau(professeurId, groupeId, salle.getId(), creneau.getId(), occupation);
                enregistrerCours(occupation, groupeId, matiereId, creneau);

                return true;
            }
        }

        return false;
    }

    private boolean existeTPMatiereCeJour(OccupationPlanning occupation, Long groupeId,
                                          Long matiereId, CreneauHoraire creneau) {
        String key = cleMatiereJour(groupeId, matiereId, creneau);
        return occupation.matieresTPParJour.contains(key);
    }

    private boolean placerTP(SeanceAPlacer aPlacer, List<CreneauHoraire> creneaux,
                             OccupationPlanning occupation, EmploiDuTemps emploi) {
        Long professeurId = aPlacer.enseignement.getProfesseur().getId();
        Long groupeId = aPlacer.groupe.getId();
        Long matiereId = aPlacer.enseignement.getMatiere().getId();

        List<List<CreneauHoraire>> paires = trouverPairesCreneauxTP(professeurId, creneaux);

        paires.sort(Comparator
                .comparingInt((List<CreneauHoraire> paire) ->
                        existeCoursMatiereCeJour(occupation, groupeId, matiereId, paire.get(0)) ? 1 : 0)
                .thenComparingInt(paire -> getChargeJour(occupation, groupeId, paire.get(0)))
                .thenComparingInt(paire -> getNombreTPJour(occupation, groupeId, paire.get(0))));

        for (List<CreneauHoraire> paire : paires) {
            CreneauHoraire premier = paire.get(0);
            CreneauHoraire deuxieme = paire.get(1);

            if (getNombreTPJour(occupation, groupeId, premier) >= 2) continue;

            List<Salle> salles = trouverSallesCompatibles(aPlacer.groupe, TypeSeance.TP);

            for (Salle salle : salles) {
                boolean premierLibre = estCreneauLibre(professeurId, groupeId, salle.getId(),
                        premier.getId(), occupation);

                boolean deuxiemeLibre = estCreneauLibre(professeurId, groupeId, salle.getId(),
                        deuxieme.getId(), occupation);

                if (!premierLibre || !deuxiemeLibre) continue;

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

                seanceRepository.save(seance1);
                seanceRepository.save(seance2);

                reserverCreneau(professeurId, groupeId, salle.getId(), premier.getId(), occupation);
                reserverCreneau(professeurId, groupeId, salle.getId(), deuxieme.getId(), occupation);

                enregistrerTP(occupation, groupeId, matiereId, premier, deuxieme);

                return true;
            }
        }

        return false;
    }

    private boolean existeCoursMatiereCeJour(OccupationPlanning occupation, Long groupeId,
                                             Long matiereId, CreneauHoraire creneau) {
        String key = cleMatiereJour(groupeId, matiereId, creneau);
        return occupation.matieresCoursParJour.contains(key);
    }

    @Transactional
    public EmploiDuTemps genererEmploiDuTemps(Long filiereId, String semestre, Long administrateurId) {

        if (semestre == null || semestre.isBlank()) {
            throw new RuntimeException("Le semestre est obligatoire");
        }

        verifierDonneesGeneration(filiereId, semestre);

        List<EmploiDuTemps> anciensEmplois =
                seanceRepository.findTimetablesByFiliereAndSemestre(filiereId, semestre);

        Administrateur administrateur = administrateurRepository.findById(administrateurId)
                .orElseThrow(() -> new RuntimeException("Administrateur introuvable"));

        List<SeanceAPlacer> seances = preparerSeances(filiereId, semestre);

        if (seances.isEmpty()) {
            throw new RuntimeException("Aucune séance à générer");
        }

        List<CreneauHoraire> creneaux = creneauHoraireRepository.findAll();

        EmploiDuTemps emploi = new EmploiDuTemps();
        emploi.setSemestre(semestre);
        emploi.setPublie(false);
        emploi.setAdministrateur(administrateur);
        emploi = emploiDuTempsRepository.save(emploi);

        OccupationPlanning occupation = new OccupationPlanning();

        List<SeanceAPlacer> cours = new ArrayList<>();
        List<SeanceAPlacer> tp = new ArrayList<>();
        List<SeanceAPlacer> seancesOrdonnees = new ArrayList<>();

        for (SeanceAPlacer s : seances) {
            if (s.type == TypeSeance.TP) {
                tp.add(s);
            } else {
                cours.add(s);
            }
        }

        int max = Math.max(cours.size(), tp.size());

        for (int i = 0; i < max; i++) {
            if (i < cours.size()) {
                seancesOrdonnees.add(cours.get(i));
            }

            if (i < tp.size()) {
                seancesOrdonnees.add(tp.get(i));
            }
        }

        seances = seancesOrdonnees;

        for (SeanceAPlacer seance : seances) {
            boolean placee;

            if (seance.type == TypeSeance.TP) {
                placee = placerTP(seance, creneaux, occupation, emploi);
            } else {
                placee = placerCours(seance, creneaux, occupation, emploi);
            }

            if (!placee) {
                throw new RuntimeException("Impossible de placer " + seance.type
                        + " pour la matière " + seance.enseignement.getMatiere().getNom()
                        + " et le groupe " + seance.groupe.getNom());
            }
        }

        if (!anciensEmplois.isEmpty()) {
            emploiDuTempsRepository.deleteAll(anciensEmplois);
            emploiDuTempsRepository.flush();
        }

        return emploi;
    }
}