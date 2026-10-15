package com.legrandecinema.le_grande_cinema.controller;

import com.legrandecinema.le_grande_cinema.model.Film;
import com.legrandecinema.le_grande_cinema.service.FilmService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FilmControllerTest {

    @Test
    void shouldReturnFilmALAffiche() throws Exception {

        FilmService filmService = Mockito.mock(FilmService.class);

        FilmController filmController = new FilmController(filmService);

        //créé un environnement http autour de FilmController sans démarrer tout Spring
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(filmController).build();

        Film film1 = new Film();
        Film film2 = new Film();

        List<Film> filmALAffiche = List.of(film1, film2);

        Mockito.when(filmService.getFilmsALAffiche())
                .thenReturn(filmALAffiche);

        mockMvc.perform(
                get("/api/films")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
        //verifie que le Json qu'il reçoit est bien du style
        //[
        //  {
        //    "idFilm": 1,
        //    "titre": "Film..."
        //  },
        //  {
        //    "idFilm": 2,
        //    "titre": "Film..."
        //  }
        //]

    }

    @Test
    void shouldReturnFilmByIdWhenFilmExists() throws Exception {

        FilmService filmService = Mockito.mock(FilmService.class);

        FilmController filmController = new FilmController(filmService);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(filmController).build();

        Film film = new Film();
        film.setTitre("Terminator");

        Mockito.when(
                filmService.getFilmById(1))
                        .thenReturn(Optional.of(film));

        mockMvc.perform(
                get("/api/films/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titre").value("Terminator"));

        Mockito.verify(filmService).getFilmById(1);

    }

    @Test
    void shouldReturnErrorWhenFilmNotFound() throws Exception {
        FilmService filmService = Mockito.mock(FilmService.class);
        FilmController filmController = new FilmController(filmService);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(filmController).build();

        Mockito.when(
                        filmService.getFilmById(1000))
                .thenReturn(Optional.empty());

        mockMvc.perform(
                        get("/api/films/1000")
                )
                .andExpect(status().isNotFound());

        Mockito.verify(filmService).getFilmById(1000);

    }
}
