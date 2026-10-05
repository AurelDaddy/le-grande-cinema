package com.legrandecinema.le_grande_cinema.service;

import com.legrandecinema.le_grande_cinema.model.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationService {

    Reservation createReservation(Reservation reservation);

    Optional<Reservation> getReservationById(Integer id);

    List<Reservation> getReservationsConfirmees();
}
