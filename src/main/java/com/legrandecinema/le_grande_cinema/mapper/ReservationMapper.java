package com.legrandecinema.le_grande_cinema.mapper;

import com.legrandecinema.le_grande_cinema.dto.ReservationDto;
import com.legrandecinema.le_grande_cinema.model.Reservation;

public class ReservationMapper {

    private ReservationMapper() {
    }
    public static ReservationDto toDto(Reservation reservation) {
        return new ReservationDto(
                reservation.getIdReservation(),
                reservation.getDateReservation(),
                reservation.getStatut(),
                reservation.getUtilisateur().getIdUtilisateur(),
                reservation.getSeance().getIdSeance()
        );
    }
}
