package com.legrandecinema.le_grande_cinema.mapper;

import com.legrandecinema.le_grande_cinema.dto.SalleDto;
import com.legrandecinema.le_grande_cinema.model.Salle;

public class SalleMapper {

    public static SalleDto toDto(Salle salle){

        return new SalleDto(
                salle.getIdSalle(),
                salle.getNumeroSalle()
        );
    }
}
