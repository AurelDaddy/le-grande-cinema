package com.legrandecinema.le_grande_cinema.repositories;

import com.legrandecinema.le_grande_cinema.model.Film;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilmRepository extends JpaRepository<Film, Integer> {
}
