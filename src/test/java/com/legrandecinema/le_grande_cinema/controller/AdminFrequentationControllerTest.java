package com.legrandecinema.le_grande_cinema.controller;

import com.legrandecinema.le_grande_cinema.service.FrequentationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminFrequentationControllerTest {

    @Test
    void shouldReturnTauxRemplissageDUneSeance() throws Exception {

        FrequentationService frequentationService = Mockito.mock(FrequentationService.class);

        AdminFrequentationController adminFrequentationController = new AdminFrequentationController(frequentationService);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(adminFrequentationController).build();

        Mockito.when(
                frequentationService.getTauxDeRemplissage(1))
                .thenReturn(20.0);

        mockMvc.perform(
                get("/api/admin/frequentation/1/seance")
        )
                .andExpect(status().isOk())
                .andExpect(content().string("20.0"));

        Mockito.verify(frequentationService).getTauxDeRemplissage(1);

    }


    @Test
    void shouldReturnTauxRemplissageParPeriode() throws Exception {

        FrequentationService frequentationService = Mockito.mock(FrequentationService.class);

        AdminFrequentationController adminFrequentationController = new AdminFrequentationController(frequentationService);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(adminFrequentationController).build();

        LocalDateTime dateDebut = LocalDateTime.of(2026,8,1,12,45);
        LocalDateTime dateFin = LocalDateTime.of(2026,8,31,12,45);

        Mockito.when(
                frequentationService.getTauxDeRemplissageParPeriode(dateDebut, dateFin))
                .thenReturn(65.0);

        mockMvc.perform(
                get("/api/admin/frequentation")
                        .param("dateDebut", "2026-08-01T12:45:00")
                        .param("dateFin", "2026-08-31T12:45:00")
        )
                .andExpect(status().isOk())
                .andExpect(content().string("65.0"));

        Mockito.verify(frequentationService)
                .getTauxDeRemplissageParPeriode(
                        dateDebut,
                        dateFin
                );
    }
}
