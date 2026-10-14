package com.legrandecinema.le_grande_cinema.repositories;

import com.legrandecinema.le_grande_cinema.model.Reservation;
import com.legrandecinema.le_grande_cinema.model.type.StatutReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    List<Reservation> findByStatut(StatutReservation statut);

    Boolean existsBySeanceIdSeance(Integer idSeance); //existsBy car pas besoin de charger toutes les réservations en mémoire, on veut juste une réponse true or false

    List<Reservation> findBySeanceIdSeance(Integer idSeance);
}
