
package com.edt.repository;

import com.edt.entity.Groupe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupeRepository extends JpaRepository<Groupe, Long> {

    List<Groupe> findByFiliereId(Long filiereId);

    @Query("""
        SELECT DISTINCT g
        FROM Groupe g
        JOIN g.semestres s
        WHERE g.filiere.id = :filiereId
          AND s = :semestre
    """)
    List<Groupe> findByFiliereIdAndSemestre(
            @Param("filiereId") Long filiereId,
            @Param("semestre") String semestre
    );
}
