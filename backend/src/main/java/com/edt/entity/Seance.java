package com.edt.entity;

import com.edt.entity.enums.StatutSeance;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "seance")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Seance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutSeance statut = StatutSeance.PLANIFIEE;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "emploi_du_temps_id", nullable = false)
    @JsonIgnore
    private EmploiDuTemps emploiDuTemps;

    @ManyToOne(optional = false)
    @JoinColumn(name = "creneau_id", nullable = false)
    private CreneauHoraire creneau;

    @ManyToOne(optional = false)
    @JoinColumn(name = "matiere_id", nullable = false)
    private Matiere matiere;

    @ManyToOne(optional = false)
    @JoinColumn(name = "salle_id", nullable = false)
    private Salle salle;

    @ManyToOne(optional = false)
    @JoinColumn(name = "groupe_id", nullable = false)
    private Groupe groupe;

    @ManyToOne(optional = false)
    @JoinColumn(name = "professeur_id", nullable = false)
    private Professeur professeur;
    public void setEmploiDuTemps(EmploiDuTemps emploiDuTemps) {
        this.emploiDuTemps = emploiDuTemps;
    }
}
