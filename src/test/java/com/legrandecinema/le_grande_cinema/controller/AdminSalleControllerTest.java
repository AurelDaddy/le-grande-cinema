package com.legrandecinema.le_grande_cinema.controller;

import com.legrandecinema.le_grande_cinema.model.Salle;
import com.legrandecinema.le_grande_cinema.service.SalleService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminSalleControllerTest {

    @Test
    void shouldReturnAllSalles() throws Exception {

        SalleService salleService =
                Mockito.mock(SalleService.class);

        AdminSalleController adminSalleController =
                new AdminSalleController(salleService);

        MockMvc mockMvc =
                MockMvcBuilders
                        .standaloneSetup(adminSalleController)
                        .build();

        Salle salle1 = new Salle();
        salle1.setIdSalle(1);
        salle1.setNumeroSalle(1);

        Salle salle2 = new Salle();
        salle2.setIdSalle(2);
        salle2.setNumeroSalle(2);

        Mockito.when(salleService.getAllSalles())
                .thenReturn(List.of(salle1, salle2));

        mockMvc.perform(
                        get("/api/admin/salles")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].idSalle").value(1))
                .andExpect(jsonPath("$[0].numeroSalle").value(1))
                .andExpect(jsonPath("$[1].idSalle").value(2))
                .andExpect(jsonPath("$[1].numeroSalle").value(2));

        Mockito.verify(salleService)
                .getAllSalles();
    }
}