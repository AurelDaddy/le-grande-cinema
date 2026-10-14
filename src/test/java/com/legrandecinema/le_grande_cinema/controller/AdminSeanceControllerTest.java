package com.legrandecinema.le_grande_cinema.controller;

import com.legrandecinema.le_grande_cinema.dto.CreateSeanceRequest;
import com.legrandecinema.le_grande_cinema.model.Film;
import com.legrandecinema.le_grande_cinema.model.Salle;
import com.legrandecinema.le_grande_cinema.model.Seance;
import com.legrandecinema.le_grande_cinema.service.FilmService;
import com.legrandecinema.le_grande_cinema.service.SalleService;
import com.legrandecinema.le_grande_cinema.service.SeanceService;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminSeanceControllerTest {

    @Test
    void shouldCreateSeance() throws Exception{

        SeanceService seanceService = Mockito.mock(SeanceService.class);
        SalleService salleService = Mockito.mock(SalleService.class);
        FilmService filmService = Mockito.mock(FilmService.class);

        AdminSeanceController adminSeanceController = new AdminSeanceController(seanceService, salleService, filmService);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(adminSeanceController).build();

        ObjectMapper objectMapper = new ObjectMapper();

        Salle salle = new Salle();
        salle.setIdSalle(1);

        Film film = new Film();
        film.setIdFilm(1);

        LocalDateTime dateHeure = LocalDateTime.of(2027,12,12,20,00,00);

        BigDecimal prix = new BigDecimal("10");

        CreateSeanceRequest request = new CreateSeanceRequest(
                dateHeure,
                "VF",
                prix,
                1,
                1
        );

        Mockito.when(
                salleService.getSalleById(1))
                        .thenReturn(Optional.of(salle));

        Mockito.when(
                filmService.getFilmById(1))
                .thenReturn(Optional.of(film));

        Mockito.when(seanceService.createSeance(Mockito.any(Seance.class)))
                .thenAnswer(invocation -> {
                    Seance seance = invocation.getArgument(0);
                    seance.setIdSeance(10);
                    return seance;
                });

        mockMvc.perform(
                        post("/api/admin/seances")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idSeance").value(10))
                .andExpect(jsonPath("$.idSalle").value(1))
                .andExpect(jsonPath("$.idFilm").value(1))
                .andExpect(jsonPath("$.version").value("VF"));


        ArgumentCaptor<Seance> captor = ArgumentCaptor.forClass(Seance.class);

        Mockito.verify(seanceService)
                .createSeance(captor.capture());

        Seance seanceCreee = captor.getValue();

        assertEquals(salle, seanceCreee.getSalle());
        assertEquals(film, seanceCreee.getFilm());
        assertEquals(dateHeure, seanceCreee.getDateHeure());
        assertEquals(prix, seanceCreee.getPrix());
        assertEquals("VF", seanceCreee.getVersion());
    }

    @Test
    void shouldDeleteSeance() throws Exception{
        SeanceService seanceService = Mockito.mock(SeanceService.class);
        SalleService salleService = Mockito.mock(SalleService.class);
        FilmService filmService = Mockito.mock(FilmService.class);

        AdminSeanceController adminSeanceController = new AdminSeanceController(seanceService,  salleService, filmService);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(adminSeanceController).build();

        mockMvc.perform(
                delete("/api/admin/seances/10")
        )
                .andExpect(status().isNoContent());

        Mockito.verify(seanceService).deleteSeance(10);

    }

}
