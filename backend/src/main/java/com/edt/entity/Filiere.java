package com.edt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "filiere")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Filiere {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String nom;

    /** Filiere 0..* --- appartient a --- 1 Departement */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "departement_id", nullable = false)
    @JsonIgnore
    private Departement departement;

    /** Groupe 0..* --- appartient a --- 1 Filiere */
    @JsonIgnore
    @OneToMany(mappedBy = "filiere")
    private List<Groupe> groupes = new ArrayList<>();
}
