package com.legrandecinema.le_grande_cinema.dto;

import java.time.LocalDateTime;
import java.util.List;

public record BilletDto(
        Integer idReservation,
        String titreFilm,
        LocalDateTime dateHeure,
        String version,
        Integer numeroSalle,
        List<String> places,
        String prenom,
        String nom,
        String referencePaiement
) {
}
