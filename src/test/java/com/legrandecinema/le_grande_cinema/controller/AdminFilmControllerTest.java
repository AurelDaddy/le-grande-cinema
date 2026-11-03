package com.legrandecinema.le_grande_cinema.controller;

import com.legrandecinema.le_grande_cinema.dto.CreateFilmRequest;
import com.legrandecinema.le_grande_cinema.model.Film;
import com.legrandecinema.le_grande_cinema.service.FilmService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminFilmControllerTest {

    @Test
    void shouldCreateFilm() throws Exception {

        FilmService filmService = Mockito.mock(FilmService.class);

        AdminFilmController controller =
                new AdminFilmController(filmService);

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        CreateFilmRequest request = new CreateFilmRequest(
                "Interstellar",
                169,
                2014,
                "Science-fiction",
                "Christopher Nolan",
                "Voyage spatial à travers un trou de ver.",
                "https://example.com/interstellar.jpg"
        );

        Mockito.when(
                filmService.createFilm(Mockito.any(Film.class))
        ).thenAnswer(invocation -> {
            Film film = invocation.getArgument(0);
            film.setIdFilm(10);
            return film;
        });

        mockMvc.perform(
                        post("/api/admin/films")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idFilm").value(10))
                .andExpect(jsonPath("$.titre").value("Interstellar"))
                .andExpect(jsonPath("$.dureeMinute").value(169))
                .andExpect(jsonPath("$.anneeSortie").value(2014))
                .andExpect(jsonPath("$.genre").value("Science-fiction"))
                .andExpect(jsonPath("$.realisateur").value("Christopher Nolan"));

        ArgumentCaptor<Film> captor =
                ArgumentCaptor.forClass(Film.class);

        Mockito.verify(filmService)
                .createFilm(captor.capture());

        Film filmCree = captor.getValue();

        assertEquals("Interstellar", filmCree.getTitre());
        assertEquals(169, filmCree.getDureeMinute());
        assertEquals(2014, filmCree.getAnneeSortie());
        assertEquals("Science-fiction", filmCree.getGenre());
        assertEquals("Christopher Nolan", filmCree.getRealisateur());
        assertEquals(
                "Voyage spatial à travers un trou de ver.",
                filmCree.getSynopsis()
        );
        assertEquals(
                "https://example.com/interstellar.jpg",
                filmCree.getUrlAffiche()
        );
    }
    @Test
    void shouldReturnAllFilms() throws Exception {

        FilmService filmService =
                Mockito.mock(FilmService.class);

        AdminFilmController controller =
                new AdminFilmController(filmService);

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        Film film1 = new Film();
        film1.setIdFilm(1);
        film1.setTitre("Interstellar");
        film1.setDureeMinute(169);
        film1.setAnneeSortie(2014);
        film1.setGenre("Science-fiction");
        film1.setRealisateur("Christopher Nolan");
        film1.setSynopsis("Voyage spatial à travers un trou de ver.");
        film1.setUrlAffiche("https://example.com/interstellar.jpg");

        Film film2 = new Film();
        film2.setIdFilm(2);
        film2.setTitre("Inception");
        film2.setDureeMinute(148);
        film2.setAnneeSortie(2010);
        film2.setGenre("Science-fiction");
        film2.setRealisateur("Christopher Nolan");
        film2.setSynopsis("Un voleur s'introduit dans les rêves.");
        film2.setUrlAffiche("https://example.com/inception.jpg");

        Mockito.when(filmService.getAllFilms())
                .thenReturn(List.of(film1, film2));

        mockMvc.perform(
                        get("/api/admin/films")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].idFilm").value(1))
                .andExpect(jsonPath("$[0].titre").value("Interstellar"))
                .andExpect(jsonPath("$[1].idFilm").value(2))
                .andExpect(jsonPath("$[1].titre").value("Inception"));

        Mockito.verify(filmService)
                .getAllFilms();
    }
}