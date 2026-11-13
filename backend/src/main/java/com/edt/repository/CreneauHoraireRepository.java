package com.edt.repository;

import com.edt.entity.*;
import com.edt.entity.enums.Jour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalTime;

import java.util.List;
import java.util.Optional;

@Repository
public interface CreneauHoraireRepository extends JpaRepository<CreneauHoraire, Long> {
    List<CreneauHoraire> findByJourOrderByHeureDebut(Jour jour);

    @Query("""
    SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
    FROM CreneauHoraire c
    WHERE c.jour = :jour
      AND c.heureDebut < :heureFin
      AND c.heureFin > :heureDebut
""")
    boolean existeChevauchement(
            @Param("jour") Jour jour,
            @Param("heureDebut") LocalTime heureDebut,
            @Param("heureFin") LocalTime heureFin
    );

    @Query("""
    SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
    FROM CreneauHoraire c
    WHERE c.jour = :jour
      AND c.heureDebut < :heureFin
      AND c.heureFin > :heureDebut
      AND c.id <> :id
""")
    boolean existeChevauchementPourModification(
            @Param("jour") Jour jour,
            @Param("heureDebut") LocalTime heureDebut,
            @Param("heureFin") LocalTime heureFin,
            @Param("id") Long id
    );

}
