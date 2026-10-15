package com.legrandecinema.le_grande_cinema.controller;

import com.legrandecinema.le_grande_cinema.dto.CreateFilmRequest;
import com.legrandecinema.le_grande_cinema.dto.FilmDto;
import com.legrandecinema.le_grande_cinema.mapper.FilmMapper;
import com.legrandecinema.le_grande_cinema.model.Film;
import com.legrandecinema.le_grande_cinema.service.FilmService;
import com.legrandecinema.le_grande_cinema.service.SeanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/films")
public class AdminFilmController {

    private final FilmService filmService;
    private SeanceService seanceService;

    public AdminFilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public ResponseEntity<FilmDto> createFilm(
            @RequestBody CreateFilmRequest request
    ) {

        Film film = new Film();

        film.setTitre(request.titre());
        film.setDureeMinute(request.dureeMinute());
        film.setAnneeSortie(request.anneeSortie());
        film.setGenre(request.genre());
        film.setRealisateur(request.realisateur());
        film.setSynopsis(request.synopsis());
        film.setUrlAffiche(request.urlAffiche());

        Film filmEnregistre = filmService.createFilm(film);

        FilmDto filmDto = FilmMapper.toDto(filmEnregistre);

        return ResponseEntity.ok(filmDto);
    }

}