package com.edt.repository;

import com.edt.entity.Disponibilite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DisponibiliteRepository
        extends JpaRepository<Disponibilite, Long> {

    List<Disponibilite> findByProfesseurEmail(String email);

    Optional<Disponibilite> findByIdAndProfesseurEmail(
            Long id, String email);

    boolean existsByProfesseurEmailAndCreneauId(
            String email, Long creneauId);

    boolean existsByProfesseurIdAndCreneauId(
            Long professeurId, Long creneauId);
}