package com.legrandecinema.le_grande_cinema.mapper;

import com.legrandecinema.le_grande_cinema.dto.FilmDto;
import com.legrandecinema.le_grande_cinema.model.Film;

public class FilmMapper {

    private FilmMapper() {
    }
    //static pour pouvoir appeler directement la méthode sur la classe
    // et pouvoir faire ensuite FilmDto dto = FilmMapper.toDto(film)
    public static FilmDto toDto(Film film) {
        return new FilmDto(
                film.getIdFilm(),
                film.getTitre(),
                film.getDureeMinute(),
                film.getAnneeSortie(),
                film.getGenre(),
                film.getRealisateur(),
                film.getSynopsis(),
                film.getUrlAffiche()
        );
    }
}
