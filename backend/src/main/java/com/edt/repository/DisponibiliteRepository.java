package com.edt.repository;

import com.edt.entity.Disponibilite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisponibiliteRepository extends JpaRepository<Disponibilite, Long> {
    List<Disponibilite> findByProfesseurId(Long professeurId);
    boolean existsByProfesseurIdAndCreneauId(Long professeurId, Long creneauId);
}
