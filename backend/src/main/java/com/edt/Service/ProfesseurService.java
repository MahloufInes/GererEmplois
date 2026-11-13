
package com.edt.service;

import com.edt.dto.ProfesseurRequest;
import com.edt.dto.ProfesseurResponse;
import com.edt.entity.Departement;
import com.edt.entity.Professeur;
import com.edt.entity.enums.Role;
import com.edt.repository.DepartementRepository;
import com.edt.repository.ProfesseurRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProfesseurService {

    private final ProfesseurRepository professeurRepository;
    private final DepartementRepository departementRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfesseurService(
            ProfesseurRepository professeurRepository,
            DepartementRepository departementRepository,
            PasswordEncoder passwordEncoder) {

        this.professeurRepository = professeurRepository;
        this.departementRepository = departementRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Afficher tous les professeurs
    @Transactional(readOnly = true)
    public List<ProfesseurResponse> afficherTous() {
        return professeurRepository.findAll()
                .stream()
                .map(this::convertirEnResponse)
                .toList();
    }

    // Ajouter un professeur
    @Transactional
    public ProfesseurResponse ajouter(ProfesseurRequest request) {

        if (professeurRepository.findByEmail(request.email()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Cet email existe déjà");
        }

        Departement departement = departementRepository.findById(
                request.departementId()
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Département introuvable"));

        Professeur professeur = new Professeur();

        professeur.setNom(request.nom());
        professeur.setPrenom(request.prenom());
        professeur.setEmail(request.email());
        professeur.setMotDePasse(
                passwordEncoder.encode(request.motDePasse())
        );
        professeur.setRole(Role.PROFESSEUR);
        professeur.setSpecialite(request.specialite());
        professeur.setDepartement(departement);

        Professeur saved = professeurRepository.save(professeur);

        return convertirEnResponse(saved);
    }

    // Modifier un professeur
    @Transactional
    public ProfesseurResponse modifier(Long id, ProfesseurRequest request) {

        Professeur professeur = professeurRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Professeur introuvable"));

        professeurRepository.findByEmail(request.email())
                .filter(p -> !p.getId().equals(id))
                .ifPresent(p -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT, "Cet email existe déjà");
                });

        Departement departement = departementRepository.findById(
                request.departementId()
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Département introuvable"));

        professeur.setNom(request.nom());
        professeur.setPrenom(request.prenom());
        professeur.setEmail(request.email());
        professeur.setSpecialite(request.specialite());
        professeur.setDepartement(departement);

        if (request.motDePasse() != null
                && !request.motDePasse().isBlank()) {
            professeur.setMotDePasse(
                    passwordEncoder.encode(request.motDePasse())
            );
        }

        return convertirEnResponse(professeurRepository.save(professeur));
    }

    // Supprimer un professeur
    @Transactional
    public void supprimer(Long id) {

        Professeur professeur = professeurRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Professeur introuvable"));

        professeurRepository.delete(professeur);
    }

    // Compter les professeurs
    @Transactional(readOnly = true)
    public long compterProfesseurs() {
        return professeurRepository.count();
    }

    // Convertir une entité en DTO Response
    private ProfesseurResponse convertirEnResponse(Professeur professeur) {

        return new ProfesseurResponse(
                professeur.getId(),
                professeur.getNom(),
                professeur.getPrenom(),
                professeur.getEmail(),
                professeur.getSpecialite(),
                professeur.getDepartement() != null
                        ? professeur.getDepartement().getId() : null,
                professeur.getDepartement() != null
                        ? professeur.getDepartement().getNom() : null
        );
    }
}
