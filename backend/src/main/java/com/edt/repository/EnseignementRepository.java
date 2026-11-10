package com.edt.repository;

import com.edt.entity.Enseignement;
import com.edt.entity.Professeur;
import com.edt.entity.Matiere;
import com.edt.entity.Filiere;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnseignementRepository
        extends JpaRepository<Enseignement, Long> {

    // Find teaching assignments by professor
    List<Enseignement> findByProfesseur(Professeur professeur);

    // Find teaching assignments by subject
    List<Enseignement> findByMatiere(Matiere matiere);

    // Find teaching assignments by filiere
    List<Enseignement> findByFiliere(Filiere filiere);

    // Find assignments by professor and filiere
    List<Enseignement> findByProfesseurAndFiliere(
            Professeur professeur,
            Filiere filiere
    );

    // Find assignments by professor and subject
    List<Enseignement> findByProfesseurAndMatiere(
            Professeur professeur,
            Matiere matiere
    );
    boolean existsByProfesseurAndMatiereAndFiliere(
            Professeur professeur,
            Matiere matiere,
            Filiere filiere
    );
}