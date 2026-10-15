package com.legrandecinema.le_grande_cinema.repositories;

import com.legrandecinema.le_grande_cinema.model.Seance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SeanceRepository extends JpaRepository<Seance, Integer> {

    List<Seance> findByFilmIdFilmAndDateHeureAfter(Integer idFilm, LocalDateTime dateHeure);
}
