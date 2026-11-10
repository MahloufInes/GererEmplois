package com.edt.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "enseignement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enseignement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professeur_id", nullable = false)
    private Professeur professeur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matiere_id", nullable = false)
    private Matiere matiere;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filiere_id", nullable = false)
    private Filiere filiere;

    @Column(nullable = false)
    private Float heuresCours;

    @Column(nullable = false)
    private Float heuresTP;
}