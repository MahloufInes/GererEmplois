
package com.edt.service;

import com.edt.dto.CreneauHoraireRequest;
import com.edt.dto.CreneauHoraireResponse;
import com.edt.entity.CreneauHoraire;
import com.edt.repository.CreneauHoraireRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CreneauHoraireService {

    private final CreneauHoraireRepository repository;

    public CreneauHoraireService(CreneauHoraireRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<CreneauHoraireResponse> afficherTous() {
        return repository.findAll().stream()
                .map(this::convertirEnResponse)
                .toList();
    }


    @Transactional
    public CreneauHoraireResponse ajouter(CreneauHoraireRequest request) {

        verifierHeures(request);

        if (repository.existeChevauchement(
                request.jour(),
                request.heureDebut(),
                request.heureFin())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ce créneau chevauche un autre créneau de ce jour."
            );
        }

        CreneauHoraire creneau = new CreneauHoraire();
        creneau.setJour(request.jour());
        creneau.setHeureDebut(request.heureDebut());
        creneau.setHeureFin(request.heureFin());

        return convertirEnResponse(repository.save(creneau));
    }



    @Transactional
    public CreneauHoraireResponse modifier(Long id, CreneauHoraireRequest request) {

        CreneauHoraire creneau = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Créneau introuvable"));

        verifierHeures(request);

        if (repository.existeChevauchementPourModification(
                request.jour(),
                request.heureDebut(),
                request.heureFin(),
                id)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ce créneau chevauche un autre créneau de ce jour."
            );
        }

        creneau.setJour(request.jour());
        creneau.setHeureDebut(request.heureDebut());
        creneau.setHeureFin(request.heureFin());

        return convertirEnResponse(repository.save(creneau));
    }


    @Transactional
    public void supprimer(Long id) {
        CreneauHoraire creneau = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Créneau introuvable"));

        repository.delete(creneau);
    }

    private void verifierHeures(CreneauHoraireRequest request) {

        if (!request.heureDebut().isBefore(request.heureFin())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "L'heure de début doit être avant l'heure de fin");
        }
    }

    private CreneauHoraireResponse convertirEnResponse(CreneauHoraire c) {
        return new CreneauHoraireResponse(
                c.getId(),
                c.getJour(),
                c.getHeureDebut(),
                c.getHeureFin()
        );
    }
}
