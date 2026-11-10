package com.edt.entity;

import com.edt.entity.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "utilisateur")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role;

    @JsonIgnore
    @ManyToMany(mappedBy = "destinataires")
    private Set<Notification> notifications = new HashSet<>();
}