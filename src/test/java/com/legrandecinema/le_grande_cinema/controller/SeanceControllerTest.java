package com.legrandecinema.le_grande_cinema.controller;

import com.legrandecinema.le_grande_cinema.model.Film;
import com.legrandecinema.le_grande_cinema.model.Salle;
import com.legrandecinema.le_grande_cinema.model.Seance;
import com.legrandecinema.le_grande_cinema.service.SeanceService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SeanceControllerTest {

    @Test
    void shouldReturnSeancesDunFilm() throws Exception {

        SeanceService seanceService = Mockito.mock(SeanceService.class);

        SeanceController seanceController = new SeanceController(seanceService);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(seanceController).build();

        Salle salle =  new Salle();
        salle.setIdSalle(1);

        Film film = new Film();
        film.setIdFilm(1);

        Seance seance1 = new Seance();
        seance1.setVersion("VF");
        seance1.setIdSeance(1);
        seance1.setFilm(film);
        seance1.setSalle(salle);
        seance1.setDateHeure(LocalDateTime.now().plusDays(1));

        Seance seance2 = new Seance();
        seance2.setVersion("VF");
        seance2.setIdSeance(1);
        seance2.setFilm(film);
        seance2.setSalle(salle);
        seance2.setDateHeure(LocalDateTime.now().plusDays(2));

        List<Seance> seancesFilm = List.of(seance1,seance2);

        Mockito.when(seanceService.getSeancesByFilm(1))
                .thenReturn(seancesFilm);

        mockMvc.perform(
                        get("/api/films/1/seances")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        Mockito.verify(seanceService).getSeancesByFilm(1);

    }

    @Test
    void shouldReturnSeanceWhenSeanceExists() throws Exception {
        SeanceService seanceService = Mockito.mock(SeanceService.class);

        SeanceController seanceController = new SeanceController(seanceService);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(seanceController).build();

        Salle salle =  new Salle();
        salle.setIdSalle(1);

        Film film = new Film();
        film.setIdFilm(1);

        Seance seance = new Seance();
        seance.setVersion("VF");
        seance.setIdSeance(1);
        seance.setFilm(film);
        seance.setSalle(salle);
        seance.setDateHeure(LocalDateTime.now().plusDays(1));

        Mockito.when(seanceService.getSeanceById(1))
                .thenReturn(Optional.of(seance));

        mockMvc.perform(
                get("/api/seances/1")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value("VF"));

        Mockito.verify(seanceService).getSeanceById(1);
    }

    @Test
    void shouldReturnErrorWhenSeanceDoesNotExist() throws Exception {
        SeanceService seanceService = Mockito.mock(SeanceService.class);
        SeanceController seanceController = new SeanceController(seanceService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(seanceController).build();

        Mockito.when(seanceService.getSeanceById(1))
                .thenReturn(Optional.empty());

        mockMvc.perform(
                get("/api/seances/1")
        )
                .andExpect(status().isNotFound());

        Mockito.verify(seanceService).getSeanceById(1);
    }
}
