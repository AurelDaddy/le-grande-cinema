package com.legrandecinema.le_grande_cinema.repositories;

import com.legrandecinema.le_grande_cinema.model.PlaceSeance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceSeanceRepository extends JpaRepository<PlaceSeance, Integer> {

    long countBySeanceIdSeanceAndReservationIsNull(Integer idSeance);
}
