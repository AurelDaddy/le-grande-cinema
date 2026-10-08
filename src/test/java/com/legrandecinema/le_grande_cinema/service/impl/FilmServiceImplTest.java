package com.legrandecinema.le_grande_cinema.service.impl;

import com.legrandecinema.le_grande_cinema.model.Film;
import com.legrandecinema.le_grande_cinema.repositories.FilmRepository;
import com.legrandecinema.le_grande_cinema.service.FilmService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmServiceImplTest {

    @Test
    void shouldReturnLesFilmsALAffiche() {
        FilmRepository filmRepository = Mockito.mock(FilmRepository.class);

        FilmService filmService = new FilmServiceImpl(filmRepository);

        Film film1 = new Film();
        Film film2 = new Film();

        List<Film> ListeFilm =  List.of(film1, film2);

        Mockito.when(filmRepository.findDistinctBySeancesDateHeureAfter(
                Mockito.any(LocalDateTime.class)))
                .thenReturn(ListeFilm);

        List<Film> ListeFilmsALAffiche = filmService.getFilmsALAffiche();

        assertEquals(ListeFilm, ListeFilmsALAffiche);

        Mockito.verify(filmRepository)
                .findDistinctBySeancesDateHeureAfter(
                        Mockito.any(LocalDateTime.class)
                );
    }
}
