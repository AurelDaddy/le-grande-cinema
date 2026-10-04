package com.legrandecinema.le_grande_cinema.model;

import com.legrandecinema.le_grande_cinema.model.type.TypeUtilisateur;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "utilisateur")
public class Utilisateur {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column( name = "id_utilisateur")
private Integer idUtilisateur;

@Column(name = "prenom", nullable = false, length = 100)
private String prenom;

@Column(name = "nom", nullable = false, length = 100)
private String nom;

@Column(name = "email", nullable = false, length = 255, unique = true)
private String email;

@Column(name = "mot_de_passe", nullable = false, length = 255)
private String motDePasse;

@Column(name = "telephone", length = 20)
private String telephone;

@Enumerated(EnumType.STRING)
@Column(name = "type_utilisateur",nullable = false, length = 20)
private TypeUtilisateur typeUtilisateur;

@OneToMany(mappedBy = "utilisateur")
private List<Reservation> reservations;

}
