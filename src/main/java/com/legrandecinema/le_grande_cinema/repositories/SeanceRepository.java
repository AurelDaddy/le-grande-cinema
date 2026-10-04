package com.legrandecinema.le_grande_cinema.repositories;

import com.legrandecinema.le_grande_cinema.model.Seance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeanceRepository extends JpaRepository<Seance, Integer> {
}
