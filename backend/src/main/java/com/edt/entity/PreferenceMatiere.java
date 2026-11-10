package com.edt.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "preference_matiere",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"professeur_id", "matiere_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreferenceMatiere {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professeur_id", nullable = false)
    private Professeur professeur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matiere_id", nullable = false)
    private Matiere matiere;
}