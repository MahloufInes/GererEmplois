
package com.edt.repository;

import com.edt.entity.Seance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SeanceRepository extends JpaRepository<Seance, Long> {

    List<Seance> findByEmploiDuTempsId(Long emploiId);

    List<Seance> findByProfesseurId(Long professeurId);

    List<Seance> findByGroupeId(Long groupeId);

    List<Seance> findByGroupeIdAndDate(
            Long groupeId,
            LocalDate date
    );

    List<Seance> findByGroupeIdAndDateBetween(
            Long groupeId,
            LocalDate debut,
            LocalDate fin
    );

    // Find existing timetables for one filiere and semester.
    @Query("""
        SELECT DISTINCT s.emploiDuTemps
        FROM Seance s
        WHERE s.groupe.filiere.id = :filiereId
        AND s.emploiDuTemps.semestre = :semestre
    """)
    List<com.edt.entity.EmploiDuTemps> findTimetablesByFiliereAndSemestre(
            @Param("filiereId") Long filiereId,
            @Param("semestre") String semestre
    );

    // Conflict detection
    boolean existsBySalleIdAndCreneauIdAndDate(
            Long salleId,
            Long creneauId,
            LocalDate date
    );

    boolean existsByProfesseurIdAndCreneauIdAndDate(
            Long professeurId,
            Long creneauId,
            LocalDate date
    );

    boolean existsByGroupeIdAndCreneauIdAndDate(
            Long groupeId,
            Long creneauId,
            LocalDate date
    );

    @Query("""
    SELECT s
    FROM Seance s
    JOIN FETCH s.creneau
    JOIN FETCH s.matiere
    JOIN FETCH s.professeur
    JOIN FETCH s.salle
    JOIN FETCH s.groupe
    JOIN FETCH s.emploiDuTemps e
    WHERE s.groupe.id = :groupeId
    AND e.publie = true
    ORDER BY s.date, s.creneau.heureDebut
""")
    List<Seance> findSeancesPublieesByGroupeId(
            @Param("groupeId") Long groupeId
    );

}
