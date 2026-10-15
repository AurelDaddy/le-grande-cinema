package com.legrandecinema.le_grande_cinema.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "film")
public class Film {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id_film")
private Integer idFilm;

@Column(name = "titre", nullable = false, length = 255 )
private String titre;

@Column(name = "duree_minute", nullable = false)
private int dureeMinute;

@Column(name = "annee_sortie", nullable = false)
private int anneeSortie;

@Column(name = "genre", nullable = false, length = 100)
private String genre;

@Column(name = "realisateur", nullable = false, length = 255 )
private String realisateur;

@Column(name = "synopsis", nullable = false)
private String synopsis;

@Column(name = "url_affiche", nullable = false, length = 500)
private String urlAffiche;

@OneToMany(mappedBy = "film")
private List<Seance> seances;




}
