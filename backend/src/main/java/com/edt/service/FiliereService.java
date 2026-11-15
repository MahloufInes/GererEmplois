
package com.edt.Service;

import com.edt.dto.FiliereRequest;
import com.edt.dto.FiliereResponse;
import com.edt.dto.FiliereMatiereRequest;
import com.edt.dto.FiliereMatiereResponse;

import com.edt.entity.Filiere;
import com.edt.entity.Departement;
import com.edt.entity.Matiere;
import com.edt.entity.FiliereMatiere;

import com.edt.repository.FiliereRepository;
import com.edt.repository.DepartementRepository;
import com.edt.repository.MatiereRepository;
import com.edt.repository.FiliereMatiereRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class FiliereService {

    private final FiliereRepository filiereRepository;
    private final DepartementRepository departementRepository;
    private final MatiereRepository matiereRepository;
    private final FiliereMatiereRepository filiereMatiereRepository;

    public FiliereService(
            FiliereRepository filiereRepository,
            DepartementRepository departementRepository,
            MatiereRepository matiereRepository,
            FiliereMatiereRepository filiereMatiereRepository) {

        this.filiereRepository = filiereRepository;
        this.departementRepository = departementRepository;
        this.matiereRepository = matiereRepository;
        this.filiereMatiereRepository = filiereMatiereRepository;
    }

    @Transactional(readOnly = true)
    public List<FiliereResponse> afficherToutes() {
        return filiereRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public FiliereResponse ajouter(FiliereRequest request) {

        if (filiereRepository.existsByNom(request.nom())) {
            throw new RuntimeException("Cette filière existe déjà.");
        }

        Departement departement = departementRepository
                .findById(request.departementId())
                .orElseThrow(() ->
                        new RuntimeException("Département introuvable."));

        Filiere filiere = new Filiere();
        filiere.setNom(request.nom().trim());
        filiere.setDepartement(departement);

        filiere = filiereRepository.save(filiere);

        enregistrerMatieres(filiere, request.matieres());

        return toResponse(filiere);
    }

    public FiliereResponse modifier(Long id, FiliereRequest request) {

        Filiere filiere = filiereRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Filière introuvable."));

        if (filiereRepository.existsByNom(request.nom())
                && !filiere.getNom().equalsIgnoreCase(request.nom())) {
            throw new RuntimeException("Cette filière existe déjà.");
        }

        Departement departement = departementRepository
                .findById(request.departementId())
                .orElseThrow(() ->
                        new RuntimeException("Département introuvable."));

        filiere.setNom(request.nom().trim());
        filiere.setDepartement(departement);

        filiereRepository.save(filiere);

        enregistrerMatieres(filiere, request.matieres());

        return toResponse(filiere);
    }

    private void enregistrerMatieres(
            Filiere filiere,
            List<FiliereMatiereRequest> matieres) {

        if (matieres == null || matieres.isEmpty()) {
            throw new RuntimeException(
                    "La liste des matières est obligatoire.");
        }

        Set<String> associations = new HashSet<>();

        for (FiliereMatiereRequest request : matieres) {

            if (request.matiereId() == null) {
                throw new RuntimeException(
                        "Une matière sélectionnée est invalide.");
            }

            if (request.semestre() == null
                    || !request.semestre().trim().matches("S[1-9][0-9]*")) {
                throw new RuntimeException(
                        "Le semestre doit avoir un format valide, par exemple S3.");
            }

            String semestre = request.semestre().trim().toUpperCase();
            String cle = request.matiereId() + "-" + semestre;

            if (!associations.add(cle)) {
                throw new RuntimeException(
                        "Cette matière est déjà ajoutée dans le semestre "
                                + semestre + ".");
            }

            if (request.heuresCours() == null
                    || request.heuresTP() == null
                    || request.heuresCours() < 0
                    || request.heuresTP() < 0) {
                throw new RuntimeException(
                        "Les heures de cours et de TP doivent être positives ou nulles.");
            }
        }

        // Validate all subjects before deleting existing associations.
        for (FiliereMatiereRequest request : matieres) {
            if (!matiereRepository.existsById(request.matiereId())) {
                throw new RuntimeException(
                        "Matière introuvable : " + request.matiereId());
            }
        }

        // Replace the previous associations.
        List<FiliereMatiere> anciennesMatieres =
                filiereMatiereRepository.findByFiliereId(filiere.getId());

        filiereMatiereRepository.deleteAll(anciennesMatieres);
        filiereMatiereRepository.flush();

        // Save the new subject-semester associations.
        for (FiliereMatiereRequest request : matieres) {

            Matiere matiere = matiereRepository
                    .findById(request.matiereId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Matière introuvable : "
                                            + request.matiereId()));

            FiliereMatiere association = new FiliereMatiere();

            association.setFiliere(filiere);
            association.setMatiere(matiere);
            association.setSemestre(
                    request.semestre().trim().toUpperCase());
            association.setHeuresCours(request.heuresCours());
            association.setHeuresTP(request.heuresTP());

            filiereMatiereRepository.save(association);
        }
    }

    public void supprimer(Long id) {

        Filiere filiere = filiereRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Filière introuvable."));

        if (!filiere.getGroupes().isEmpty()) {
            throw new RuntimeException(
                    "Impossible de supprimer cette filière : elle possède des groupes.");
        }

        List<FiliereMatiere> matieres =
                filiereMatiereRepository.findByFiliereId(id);

        filiereMatiereRepository.deleteAll(matieres);

        filiereRepository.delete(filiere);
    }

    private FiliereResponse toResponse(Filiere filiere) {

        List<FiliereMatiereResponse> matieres =
                filiereMatiereRepository.findByFiliereId(filiere.getId())
                        .stream()
                        .map(association -> new FiliereMatiereResponse(
                                association.getId(),
                                filiere.getId(),
                                association.getMatiere().getId(),
                                association.getMatiere().getNom(),
                                association.getSemestre(),
                                association.getHeuresCours(),
                                association.getHeuresTP()
                        ))
                        .toList();

        return new FiliereResponse(
                filiere.getId(),
                filiere.getNom(),
                filiere.getDepartement().getId(),
                matieres
        );
    }
}
