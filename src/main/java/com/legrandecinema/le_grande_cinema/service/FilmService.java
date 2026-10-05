package com.legrandecinema.le_grande_cinema.service;

import com.legrandecinema.le_grande_cinema.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmService {

    List<Film> getAllFilms();

    List<Film> getFilmsALAffiche();

    Optional<Film> getFilmById(Integer id);

    Film createFilm(Film film);

    //void deleteFilm(Integer id);
}
