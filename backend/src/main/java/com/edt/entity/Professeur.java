package com.edt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "professeur")
@PrimaryKeyJoinColumn(name = "id")
@Getter @Setter @NoArgsConstructor
public class Professeur extends Utilisateur {
    @OneToMany(mappedBy = "professeur")
    @JsonIgnore
    private List<PreferenceMatiere> preferencesMatieres = new ArrayList<>();
    @OneToMany(mappedBy = "professeur")
    @JsonIgnore
    private List<Enseignement> enseignements = new ArrayList<>();

    @Column(length = 100)
    private String specialite;

    /** Professeur 0..* --- rattache a --- 1 Departement */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departement_id")
    @JsonIgnore
    private Departement departement;

    /** Professeur 1 --- declare --- 0..* Indisponibilite */
    @JsonIgnore
    @OneToMany(mappedBy = "professeur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Disponibilite> disponibilites = new ArrayList<>();

    /** Seance 0..* --- assuree par --- 1 Professeur */
    @JsonIgnore
    @OneToMany(mappedBy = "professeur")
    private List<Seance> seances = new ArrayList<>();
}
