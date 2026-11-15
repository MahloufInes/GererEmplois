
package com.edt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "groupe")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Groupe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nom;

    private Integer effectif;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "filiere_id", nullable = false)
    @JsonIgnore
    private Filiere filiere;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "groupe_semestre",
            joinColumns = @JoinColumn(name = "groupe_id"),
            uniqueConstraints = @UniqueConstraint(
                    columnNames = {"groupe_id", "semestre"}
            )
    )
    @Column(name = "semestre", nullable = false, length = 2)
    private Set<String> semestres = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "groupe")
    private List<Etudiant> etudiants = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "groupe")
    private List<Seance> seances = new ArrayList<>();
}
