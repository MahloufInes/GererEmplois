
package com.edt.repository;

import com.edt.entity.FiliereMatiere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FiliereMatiereRepository
        extends JpaRepository<FiliereMatiere, Long> {

    // Find all subjects assigned to a filiere
    List<FiliereMatiere> findByFiliereId(Long filiereId);

    // Find subjects assigned to a filiere for a specific semester
    List<FiliereMatiere> findByFiliereIdAndSemestre(
            Long filiereId,
            String semestre
    );

    // Find all filieres using a particular subject
    List<FiliereMatiere> findByMatiereId(Long matiereId);

    // Check whether a subject is already assigned to a filiere
    boolean existsByFiliereIdAndMatiereId(
            Long filiereId,
            Long matiereId
    );

    // Find one specific filiere-subject association
    Optional<FiliereMatiere> findByFiliereIdAndMatiereId(
            Long filiereId,
            Long matiereId
    );

    // Delete all subject associations of a filiere
    void deleteByFiliereId(Long filiereId);
}
