package com.legrandecinema.le_grande_cinema.repositories;

import com.legrandecinema.le_grande_cinema.model.Film;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface FilmRepository extends JpaRepository<Film, Integer> {

//find        → je veux chercher
//Distinct    → sans doublons
//By          → voici le critère de recherche
//Seances     → navigue dans l'attribut "seances" de Film
//DateHeure   → puis dans l'attribut "dateHeure" de Seance
//After       → avec une valeur strictement après celle passée en paramètre

    List<Film> findDistinctBySeancesDateHeureAfter(LocalDateTime dateHeure);
}
