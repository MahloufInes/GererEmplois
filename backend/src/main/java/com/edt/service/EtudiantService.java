
package com.edt.service;

import com.edt.dto.EtudiantRequest;
import com.edt.dto.EtudiantResponse;
import com.edt.dto.EtudiantCreationResponse;
import com.edt.entity.Etudiant;
import com.edt.entity.Groupe;
import com.edt.entity.enums.Role;
import com.edt.repository.EtudiantRepository;
import com.edt.repository.GroupeRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
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
    @Transactional(readOnly = true)
    public EtudiantResponse afficherMonProfil(String email) {
        Etudiant etudiant = etudiantRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Étudiant introuvable"));

        return convertir(etudiant);
    }
    // Afficher tous les étudiants
    @Transactional(readOnly = true)
    public List<EtudiantResponse> afficherTous() {
        return etudiantRepository.findAll()
                .stream()
                .map(this::convertir)
                .toList();
    }

    // Ajouter un étudiant avec mot de passe automatique
    @Transactional
    public EtudiantCreationResponse ajouter(EtudiantRequest request) {

        // Vérifier si l'email existe déjà
        if (etudiantRepository.findByEmail(request.email()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cet email existe déjà."
            );
        }

        // Vérifier si le matricule existe déjà
        if (etudiantRepository.findByMatricule(request.matricule()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ce matricule existe déjà."
            );
        }

        // Vérifier le groupe
        Groupe groupe = groupeRepository.findById(request.groupeId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Groupe introuvable."
                ));

        // Générer un mot de passe temporaire
        String motDePasseTemporaire = genererMotDePasse();

        Etudiant e = new Etudiant();

        e.setNom(request.nom());
        e.setPrenom(request.prenom());
        e.setEmail(request.email());

        // Enregistrer le mot de passe crypté
        e.setMotDePasse(
                passwordEncoder.encode(motDePasseTemporaire)
        );

        e.setRole(Role.ETUDIANT);
        e.setMatricule(request.matricule());
        e.setGroupe(groupe);

        Etudiant saved = etudiantRepository.save(e);

        // Retourner l'étudiant et son mot de passe temporaire
        return new EtudiantCreationResponse(
                convertir(saved),
                motDePasseTemporaire
        );
    }

    // Génération d'un mot de passe temporaire aléatoire
    private String genererMotDePasse() {

        String caracteres =
                "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789@#";

        SecureRandom random = new SecureRandom();
        StringBuilder motDePasse = new StringBuilder();

        for (int i = 0; i < 12; i++) {
            motDePasse.append(
                    caracteres.charAt(random.nextInt(caracteres.length()))
            );
        }

        return motDePasse.toString();
    }

    // Modifier un étudiant
    @Transactional
    public EtudiantResponse modifier(Long id, EtudiantRequest request) {

        Etudiant e = etudiantRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Étudiant introuvable."
                ));

        // Vérifier si l'email appartient à un autre étudiant
        etudiantRepository.findByEmail(request.email())
                .filter(existant -> !existant.getId().equals(id))
                .ifPresent(existant -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Cet email existe déjà."
                    );
                });

        // Vérifier si le matricule appartient à un autre étudiant
        etudiantRepository.findByMatricule(request.matricule())
                .filter(existant -> !existant.getId().equals(id))
                .ifPresent(existant -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Ce matricule existe déjà."
                    );
                });

        Groupe groupe = groupeRepository.findById(request.groupeId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Groupe introuvable."
                ));

        e.setNom(request.nom());
        e.setPrenom(request.prenom());
        e.setEmail(request.email());
        e.setMatricule(request.matricule());
        e.setGroupe(groupe);

        // Modifier le mot de passe uniquement s'il est renseigné
        if (request.motDePasse() != null
                && !request.motDePasse().isBlank()) {

            e.setMotDePasse(
                    passwordEncoder.encode(request.motDePasse())
            );
        }

        return convertir(etudiantRepository.save(e));
    }

    // Supprimer un étudiant
    @Transactional
    public void supprimer(Long id) {

        Etudiant e = etudiantRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Étudiant introuvable."
                ));

        etudiantRepository.delete(e);
    }

    // Convertir une entité en DTO Response
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
