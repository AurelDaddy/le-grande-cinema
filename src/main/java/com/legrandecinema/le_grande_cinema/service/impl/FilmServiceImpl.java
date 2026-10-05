package com.legrandecinema.le_grande_cinema.service.impl;

import com.legrandecinema.le_grande_cinema.model.Film;
import com.legrandecinema.le_grande_cinema.repositories.FilmRepository;
import com.legrandecinema.le_grande_cinema.service.FilmService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FilmServiceImpl implements FilmService {

    // Référence vers l'instance de FilmRepository injectée par Spring
    private final FilmRepository filmRepository;

    //Injection de FilmRepository par le constructeur
    public FilmServiceImpl(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    @Override
    public List<Film>  getAllFilms() {
        return filmRepository.findAll();
    }

    @Override
    public Optional<Film> getFilmById(Integer id) {
        return filmRepository.findById(id);
    }

    @Override
    public List<Film> getFilmsALAffiche() {
        return filmRepository.findDistinctBySeancesDateHeureAfter(LocalDateTime.now());
    }

    @Override
    public Film createFilm(Film film) {
        return filmRepository.save(film);
    }




}
