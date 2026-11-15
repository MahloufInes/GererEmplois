package com.edt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(
        name = "disponibilite",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"professeur_id", "creneau_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Disponibilite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "professeur_id", nullable = false)
    private Professeur professeur;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creneau_id", nullable = false)
    private CreneauHoraire creneau;
}