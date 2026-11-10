package com.edt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "matiere")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Matiere {
    @OneToMany(mappedBy = "matiere")
    @JsonIgnore
    private List<PreferenceMatiere> preferencesProfesseurs = new ArrayList<>();

    @OneToMany(mappedBy = "matiere")
    @JsonIgnore
    private List<Enseignement> enseignements = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nom;

    /** Matiere 0..* --- geree par --- 1 Departement */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departement_id")
    @JsonIgnore
    private Departement departement;
}
