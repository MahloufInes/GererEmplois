package com.edt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "etudiant")
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
@NoArgsConstructor
public class Etudiant extends Utilisateur {

    @Column(nullable = false, unique = true, length = 50)
    private String matricule;

    /**
     * Etudiant 0..* --- regroupe --- 1 Groupe
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupe_id")
    @JsonIgnore
    private Groupe groupe;
}