package com.legrandecinema.le_grande_cinema.mapper;

import com.legrandecinema.le_grande_cinema.dto.SeanceDto;
import com.legrandecinema.le_grande_cinema.model.Seance;

public class SeanceMapper {

    private SeanceMapper(){
    }
    public static SeanceDto toDto(Seance seance){
        return new SeanceDto(
                seance.getIdSeance(),
                seance.getDateHeure(),
                seance.getVersion(),
                seance.getPrix(),
                seance.getSalle().getIdSalle(),
                seance.getFilm().getIdFilm()
        );
    }
}
