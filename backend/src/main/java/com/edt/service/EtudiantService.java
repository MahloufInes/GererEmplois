
package com.edt.service;

import com.edt.dto.EtudiantRequest;
import com.edt.dto.EtudiantResponse;
import com.edt.entity.Etudiant;
import com.edt.entity.Groupe;
import com.edt.entity.enums.Role;
import com.edt.repository.EtudiantRepository;
import com.edt.repository.GroupeRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EtudiantService {

    private final EtudiantRepository etudiantRepository;
    private final GroupeRepository groupeRepository;
    private final PasswordEncoder passwordEncoder;

    public EtudiantService(
            EtudiantRepository etudiantRepository,
            GroupeRepository groupeRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.etudiantRepository = etudiantRepository;
        this.groupeRepository = groupeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public EtudiantResponse ajouter(EtudiantRequest request) {
        if (request.motDePasse() == null || request.motDePasse().isBlank()) {
            throw new RuntimeException("Le mot de passe est obligatoire.");
        }
        if (etudiantRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Cet email existe déjà.");
        }

        if (etudiantRepository.findByMatricule(request.matricule()).isPresent()) {
            throw new RuntimeException("Ce matricule existe déjà.");
        }

        Groupe groupe = groupeRepository.findById(request.groupeId())
                .orElseThrow(() -> new RuntimeException("Groupe introuvable."));

        Etudiant e = new Etudiant();

        e.setNom(request.nom());
        e.setPrenom(request.prenom());
        e.setEmail(request.email());
        e.setMotDePasse(passwordEncoder.encode(request.motDePasse()));
        e.setRole(Role.ETUDIANT);
        e.setMatricule(request.matricule());
        e.setGroupe(groupe);

        Etudiant saved = etudiantRepository.save(e);

        return convertir(saved);
    }
    @Transactional
    public EtudiantResponse modifier(Long id, EtudiantRequest request) {

        Etudiant e = etudiantRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Étudiant introuvable."));

        // Vérifier si l'email appartient à un autre étudiant
        etudiantRepository.findByEmail(request.email())
                .filter(existant -> !existant.getId().equals(id))
                .ifPresent(existant -> {
                    throw new RuntimeException("Cet email existe déjà.");
                });

        // Vérifier si le matricule appartient à un autre étudiant
        etudiantRepository.findByMatricule(request.matricule())
                .filter(existant -> !existant.getId().equals(id))
                .ifPresent(existant -> {
                    throw new RuntimeException("Ce matricule existe déjà.");
                });

        Groupe groupe = groupeRepository.findById(request.groupeId())
                .orElseThrow(() ->
                        new RuntimeException("Groupe introuvable."));

        e.setNom(request.nom());
        e.setPrenom(request.prenom());
        e.setEmail(request.email());
        e.setMatricule(request.matricule());
        e.setGroupe(groupe);

        // Modifier le mot de passe uniquement s'il est renseigné
        if (request.motDePasse() != null
                && !request.motDePasse().isBlank()) {
            e.setMotDePasse(passwordEncoder.encode(request.motDePasse()));
        }

        return convertir(etudiantRepository.save(e));
    }

    @Transactional
    public void supprimer(Long id) {

        Etudiant e = etudiantRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Étudiant introuvable."));

        etudiantRepository.delete(e);
    }
    @Transactional(readOnly = true)
    public List<EtudiantResponse> afficherTous() {
        return etudiantRepository.findAll()
                .stream()
                .map(this::convertir)
                .toList();
    }

    private EtudiantResponse convertir(Etudiant e) {
        return new EtudiantResponse(
                e.getId(),
                e.getNom(),
                e.getPrenom(),
                e.getEmail(),
                e.getMatricule(),
                e.getGroupe().getId(),
                e.getGroupe().getNom(),
                e.getGroupe().getFiliere().getNom()
        );
    }
}
