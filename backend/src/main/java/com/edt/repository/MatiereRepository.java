package com.edt.repository;

import com.edt.entity.Matiere;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface MatiereRepository extends JpaRepository<Matiere, Long> {
    List<Matiere> findByDepartementId(Long departementId);
    List<Matiere> findByNomContainingIgnoreCase(String nom);
    @Query("""
    SELECT DISTINCT e.matiere
    FROM Enseignement e
    WHERE e.groupe.id = :groupeId
    ORDER BY e.matiere.nom
""")
    List<Matiere> findMatieresByGroupeId(
            @Param("groupeId") Long groupeId
    );
}