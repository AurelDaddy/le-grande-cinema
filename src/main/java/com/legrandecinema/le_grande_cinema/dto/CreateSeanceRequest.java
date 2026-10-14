package com.legrandecinema.le_grande_cinema.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateSeanceRequest(
        LocalDateTime dateHeure,
        String version,
        BigDecimal prix,
        Integer idSalle,
        Integer idFilm
) {
}
