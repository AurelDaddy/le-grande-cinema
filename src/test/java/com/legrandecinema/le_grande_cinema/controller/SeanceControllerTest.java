package com.legrandecinema.le_grande_cinema.controller;

import com.legrandecinema.le_grande_cinema.model.Seance;
import com.legrandecinema.le_grande_cinema.service.SeanceService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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

        Seance seance1 = new Seance();
        Seance seance2 = new Seance();

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

        Seance seance = new Seance();
        seance.setVersion("VF");

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
