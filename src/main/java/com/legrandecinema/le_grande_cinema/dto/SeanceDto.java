package com.legrandecinema.le_grande_cinema.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SeanceDto(
        Integer idSeance,
        LocalDateTime dateHeure,
        String version,
        BigDecimal prix,
        Integer idFilm,
        Integer idSalle
        ) {


}
