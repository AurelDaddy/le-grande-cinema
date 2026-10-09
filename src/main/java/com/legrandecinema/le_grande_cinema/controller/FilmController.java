package com.legrandecinema.le_grande_cinema.controller;

import com.legrandecinema.le_grande_cinema.dto.FilmDto;
import com.legrandecinema.le_grande_cinema.mapper.FilmMapper;
import com.legrandecinema.le_grande_cinema.model.Film;
import com.legrandecinema.le_grande_cinema.service.FilmService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/films")
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<FilmDto> getFilmsAlAffiche() {
        return filmService.getFilmsALAffiche()
                .stream()
                .map(FilmMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FilmDto> getFilmById(@PathVariable Integer id) {
        return filmService.getFilmById(id)
                .map(FilmMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
