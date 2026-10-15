package com.legrandecinema.le_grande_cinema.controller;

import com.legrandecinema.le_grande_cinema.dto.CreatePaiementRequest;
import com.legrandecinema.le_grande_cinema.model.Paiement;
import com.legrandecinema.le_grande_cinema.model.Reservation;
import com.legrandecinema.le_grande_cinema.model.type.StatutPaiement;
import com.legrandecinema.le_grande_cinema.service.PaiementService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PaiementControllerTest {

    @Test
    void shouldMakePaiement() throws Exception {

        PaiementService paiementService = Mockito.mock(PaiementService.class);

        PaiementController paiementController = new PaiementController(paiementService);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(paiementController).build();

        ObjectMapper objectMapper = new ObjectMapper();

        Reservation reservation = new Reservation();
        reservation.setIdReservation(2);

        Paiement paiement = new Paiement();
        paiement.setIdPaiement(5);
        paiement.setMontant(new BigDecimal("20"));
        paiement.setDatePaiement(LocalDateTime.now());
        paiement.setStatut(StatutPaiement.ACCEPTE);
        paiement.setReservation(reservation);


        Mockito.when(paiementService.effectuerPaiement("michel.dumont@test.fr",2))
                .thenReturn(paiement);

        CreatePaiementRequest request =
                new CreatePaiementRequest(2);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "michel.dumont@test.fr",
                        null
                );


        mockMvc.perform(
                post("/api/paiements")
                        .contentType("application/json")
                        .principal(authentication)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPaiement").value(5))
                .andExpect(jsonPath("$.montant").value(20.00))
                .andExpect(jsonPath("$.statut").value("ACCEPTE"))
                .andExpect(jsonPath("$.idReservation").value(2));


        Mockito.verify(paiementService)
                .effectuerPaiement("michel.dumont@test.fr",2);
    }
}
