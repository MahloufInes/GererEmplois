
package com.edt.service;

import com.edt.dto.SalleRequest;
import com.edt.dto.SalleResponse;
import com.edt.entity.Salle;
import com.edt.repository.SalleRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SalleService {

    private final SalleRepository salleRepository;

    public SalleService(SalleRepository salleRepository) {
        this.salleRepository = salleRepository;
    }

    @Transactional(readOnly = true)
    public List<SalleResponse> afficherToutes() {
        return salleRepository.findAll()
                .stream()
                .map(this::convertirEnResponse)
                .toList();
    }

    @Transactional
    public SalleResponse ajouter(SalleRequest request) {

        if (salleRepository.findByNom(request.nom()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Cette salle existe déjà"
            );
        }

        Salle salle = new Salle();
        salle.setNom(request.nom());
        salle.setCapacite(request.capacite());
        salle.setType(request.type());

        return convertirEnResponse(salleRepository.save(salle));
    }

    @Transactional
    public SalleResponse modifier(Long id, SalleRequest request) {

        Salle salle = salleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Salle introuvable"
                ));

        salleRepository.findByNom(request.nom())
                .filter(s -> !s.getId().equals(id))
                .ifPresent(s -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT, "Ce nom existe déjà"
                    );
                });

        salle.setNom(request.nom());
        salle.setCapacite(request.capacite());
        salle.setType(request.type());

        return convertirEnResponse(salleRepository.save(salle));
    }

    @Transactional
    public void supprimer(Long id) {

        Salle salle = salleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Salle introuvable"
                ));

        salleRepository.delete(salle);
    }

    @Transactional(readOnly = true)
    public long compterSalles() {
        return salleRepository.count();
    }

    private SalleResponse convertirEnResponse(Salle salle) {
        return new SalleResponse(
                salle.getId(),
                salle.getNom(),
                salle.getCapacite(),
                salle.getType()
        );
    }
}
