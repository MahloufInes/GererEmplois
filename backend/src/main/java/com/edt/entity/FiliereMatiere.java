
package com.edt.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "filiere_matiere",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"filiere_id", "semestre", "matiere_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FiliereMatiere {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "filiere_id", nullable = false)
    private Filiere filiere;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matiere_id", nullable = false)
    private Matiere matiere;

    @Column(name = "semestre", length = 10)
    private String semestre;

    @Column(nullable = false)
    private Float heuresCours;

    @Column(nullable = false)
    private Float heuresTP;
}
