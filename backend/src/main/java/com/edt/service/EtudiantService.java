
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
