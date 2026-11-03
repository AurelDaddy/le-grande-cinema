package com.legrandecinema.le_grande_cinema.controller;

import com.legrandecinema.le_grande_cinema.dto.BilletDto;
import com.legrandecinema.le_grande_cinema.service.BilletService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BilletControllerTest {

    @Test
    void shouldReturnBilletsUtilisateur() throws Exception {

        BilletService billetService =
                Mockito.mock(BilletService.class);

        BilletController billetController =
                new BilletController(billetService);

        MockMvc mockMvc =
                MockMvcBuilders
                        .standaloneSetup(billetController)
                        .build();

        String email = "michel@test.fr";

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        email,
                        null
                );

        BilletDto billet = new BilletDto(
                1,
                "Interstellar",
                LocalDateTime.of(2026, 11, 10, 20, 0),
                "VO",
                2,
                List.of("A1", "A2"),
                "Michel",
                "Dupont",
                "CINE12345678"
        );

        Mockito.when(
                billetService.getBilletsUtilisateur(email)
        ).thenReturn(List.of(billet));

        mockMvc.perform(
                        get("/api/billets")
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].idReservation").value(1))
                .andExpect(jsonPath("$[0].titreFilm").value("Interstellar"))
                .andExpect(jsonPath("$[0].version").value("VO"))
                .andExpect(jsonPath("$[0].numeroSalle").value(2))
                .andExpect(jsonPath("$[0].places.length()").value(2))
                .andExpect(jsonPath("$[0].places[0]").value("A1"))
                .andExpect(jsonPath("$[0].places[1]").value("A2"))
                .andExpect(jsonPath("$[0].prenom").value("Michel"))
                .andExpect(jsonPath("$[0].nom").value("Dupont"))
                .andExpect(jsonPath("$[0].referencePaiement").value("CINE12345678"));

        Mockito.verify(billetService)
                .getBilletsUtilisateur(email);
    }

    @Test
    void shouldReturnBilletByReservation() throws Exception {

        BilletService billetService =
                Mockito.mock(BilletService.class);

        BilletController billetController =
                new BilletController(billetService);

        MockMvc mockMvc =
                MockMvcBuilders
                        .standaloneSetup(billetController)
                        .build();

        String email = "michel@test.fr";

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        email,
                        null
                );

        BilletDto billet = new BilletDto(
                5,
                "Inception",
                LocalDateTime.of(2026, 11, 12, 21, 30),
                "VF",
                3,
                List.of("B5"),
                "Michel",
                "Dupont",
                "CINE87654321"
        );

        Mockito.when(
                billetService.getBilletByReservation(
                        email,
                        5
                )
        ).thenReturn(billet);

        mockMvc.perform(
                        get("/api/billets/5")
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idReservation").value(5))
                .andExpect(jsonPath("$.titreFilm").value("Inception"))
                .andExpect(jsonPath("$.version").value("VF"))
                .andExpect(jsonPath("$.numeroSalle").value(3))
                .andExpect(jsonPath("$.places[0]").value("B5"))
                .andExpect(jsonPath("$.referencePaiement").value("CINE87654321"));

        Mockito.verify(billetService)
                .getBilletByReservation(
                        email,
                        5
                );
    }
}