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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}