package com.legrandecinema.le_grande_cinema.controller;

import com.legrandecinema.le_grande_cinema.dto.CreateReservationRequest;
import com.legrandecinema.le_grande_cinema.model.Reservation;
import com.legrandecinema.le_grande_cinema.model.Seance;
import com.legrandecinema.le_grande_cinema.model.Utilisateur;
import com.legrandecinema.le_grande_cinema.model.type.StatutReservation;
import com.legrandecinema.le_grande_cinema.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReservationControllerTest {

    @Test
    void shouldReturnCreatedReservation() throws Exception {


        ReservationService reservationService = Mockito.mock(ReservationService.class);

        ReservationController reservationController = new ReservationController(reservationService);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(reservationController).build();

        ObjectMapper objectMapper = new ObjectMapper();

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setIdUtilisateur(1);

        Seance seance = new Seance();
        seance.setIdSeance(2);

        Reservation reservation = new Reservation();
        reservation.setIdReservation(10);
        reservation.setUtilisateur(utilisateur);
        reservation.setSeance(seance);
        reservation.setDateReservation(LocalDateTime.now());
        reservation.setStatut(StatutReservation.EN_ATTENTE);

        //on mock la création de la réservation et on retourne reservation
        Mockito.when(reservationService.createReservation("test@test.fr", 2, 2))
                .thenReturn(reservation);
        //creation de la CreateReservationRequest avec la seance et le nombre de places voulu
        CreateReservationRequest request = new CreateReservationRequest(2, 2);

        mockMvc.perform(
                post("/api/reservations")
                        .principal(
                                new UsernamePasswordAuthenticationToken("test@test.fr", null) //implementation de authentication. ici "test@test.fr" est le principal(l'identité de l'utilisateur) et null le credential(normalement le mot de passe. Ici pas nécessaire car on ne test pas Bcrypt ou le login masi seulement le comportement du controller
                        )
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))//transform l'objet request en json
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idReservation").value(10))
                .andExpect(jsonPath("$.idUtilisateur").value(1))
                .andExpect(jsonPath("$.idSeance").value(2))
                .andExpect(jsonPath("$.statut").value("EN_ATTENTE"));

        Mockito.verify(reservationService).createReservation("test@test.fr",  2, 2);


    }
}
