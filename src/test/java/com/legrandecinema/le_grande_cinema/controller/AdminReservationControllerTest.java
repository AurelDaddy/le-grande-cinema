package com.legrandecinema.le_grande_cinema.controller;

import com.legrandecinema.le_grande_cinema.model.Reservation;
import com.legrandecinema.le_grande_cinema.model.Seance;
import com.legrandecinema.le_grande_cinema.model.Utilisateur;
import com.legrandecinema.le_grande_cinema.model.type.StatutReservation;
import com.legrandecinema.le_grande_cinema.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminReservationControllerTest {

  @Test
    void shouldReturnReservationBySeance() throws Exception {

    ReservationService reservationService = Mockito.mock(ReservationService.class);

    AdminReservationController adminReservationController = new AdminReservationController(reservationService);

    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(adminReservationController).build();

    Seance seance = new Seance();
    seance.setIdSeance(3);

    Utilisateur utilisateur1 = new Utilisateur();
    utilisateur1.setIdUtilisateur(10);

    Utilisateur utilisateur2 = new Utilisateur();
    utilisateur2.setIdUtilisateur(11);

    Reservation reservation1 = new Reservation();
    reservation1.setIdReservation(2);
    reservation1.setUtilisateur(utilisateur1);
    reservation1.setSeance(seance);
    reservation1.setStatut(StatutReservation.CONFIRMEE);
    reservation1.setDateReservation(LocalDateTime.now());

    Reservation reservation2 = new Reservation();
    reservation2.setIdReservation(3);
    reservation2.setUtilisateur(utilisateur2);
    reservation2.setSeance(seance);
    reservation2.setStatut(StatutReservation.CONFIRMEE);
    reservation2.setDateReservation(LocalDateTime.now());

    Mockito.when(reservationService.getReservationsBySeance(3))
            .thenReturn(List.of(reservation1, reservation2));

    mockMvc.perform(
            get("/api/admin/seances/3/reservations")
    )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].idReservation").value(2))
            .andExpect(jsonPath("$[0].idSeance").value(3))
            .andExpect(jsonPath("$[0].statut").value("CONFIRMEE"))
            .andExpect(jsonPath("$[0].idUtilisateur").value(10))
            .andExpect(jsonPath("$[1].idReservation").value(3))
            .andExpect(jsonPath("$[1].idSeance").value(3));

    Mockito.verify(reservationService)
            .getReservationsBySeance(3);

  }
}
