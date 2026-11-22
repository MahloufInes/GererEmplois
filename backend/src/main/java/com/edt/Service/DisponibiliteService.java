
package com.edt.service;

import com.edt.dto.DisponibiliteRequest;
import com.edt.dto.DisponibiliteResponse;
import com.edt.entity.Disponibilite;
import com.edt.entity.Professeur;
import com.edt.entity.CreneauHoraire;
import com.edt.repository.DisponibiliteRepository;
import com.edt.repository.ProfesseurRepository;
import com.edt.repository.CreneauHoraireRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DisponibiliteService {

    private final DisponibiliteRepository disponibiliteRepository;
    private final ProfesseurRepository professeurRepository;
    private final CreneauHoraireRepository creneauRepository;

    public DisponibiliteService(
            DisponibiliteRepository disponibiliteRepository,
            ProfesseurRepository professeurRepository,
            CreneauHoraireRepository creneauRepository) {
        this.disponibiliteRepository = disponibiliteRepository;
        this.professeurRepository = professeurRepository;
        this.creneauRepository = creneauRepository;
    }

    private Professeur trouverProfesseur(String email) {
        return professeurRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Professeur introuvable"));
    }

    private CreneauHoraire trouverCreneau(Long id) {
        return creneauRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Créneau introuvable"));
    }

    private DisponibiliteResponse convertir(Disponibilite d) {
        return new DisponibiliteResponse(
                d.getId(),
                d.getCreneau().getId(),
                d.getCreneau().getJour().name(),
                d.getCreneau().getHeureDebut().toString(),
                d.getCreneau().getHeureFin().toString()
        );
    }

    @Transactional(readOnly = true)
    public List<DisponibiliteResponse> afficher(String email) {
        return disponibiliteRepository.findByProfesseurEmail(email)
                .stream()
                .map(this::convertir)
                .toList();
    }

    @Transactional
    public DisponibiliteResponse ajouter(
            String email, DisponibiliteRequest request) {

        Professeur professeur = trouverProfesseur(email);

        if (disponibiliteRepository.existsByProfesseurEmailAndCreneauId(
                email, request.creneauId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ce créneau est déjà ajouté");
        }

        Disponibilite d = new Disponibilite();
        d.setProfesseur(professeur);
        d.setCreneau(trouverCreneau(request.creneauId()));

        return convertir(disponibiliteRepository.save(d));
    }

    @Transactional
    public DisponibiliteResponse modifier(
            String email, Long id, DisponibiliteRequest request) {

        Disponibilite d = disponibiliteRepository
                .findByIdAndProfesseurEmail(id, email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Disponibilité introuvable"));

        if (disponibiliteRepository.existsByProfesseurEmailAndCreneauId(
                email, request.creneauId())
                && !d.getCreneau().getId().equals(request.creneauId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ce créneau est déjà ajouté");
        }

        d.setCreneau(trouverCreneau(request.creneauId()));

        return convertir(disponibiliteRepository.save(d));
    }

    @Transactional
    public void supprimer(String email, Long id) {
        Disponibilite d = disponibiliteRepository
                .findByIdAndProfesseurEmail(id, email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Disponibilité introuvable"));

        disponibiliteRepository.delete(d);
    }
}
