package com.legrandecinema.le_grande_cinema.dto;

import com.legrandecinema.le_grande_cinema.model.type.StatutReservation;

import java.time.LocalDateTime;

public record ReservationDto(
        Integer idReservation,
        LocalDateTime dateReservation,
        StatutReservation statut,
        Integer idUtilisateur,
        Integer idSeance)

{

}
