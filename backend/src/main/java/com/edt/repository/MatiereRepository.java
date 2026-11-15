package com.edt.repository;

import com.edt.entity.Matiere;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MatiereRepository extends JpaRepository<Matiere, Long> {
    List<Matiere> findByDepartementId(Long departementId);
    List<Matiere> findByNomContainingIgnoreCase(String nom);
}