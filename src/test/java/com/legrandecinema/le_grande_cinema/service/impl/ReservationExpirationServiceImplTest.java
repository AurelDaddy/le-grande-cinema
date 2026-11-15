
package com.legrandecinema.le_grande_cinema.service.impl;

import com.legrandecinema.le_grande_cinema.model.PlaceSeance;
import com.legrandecinema.le_grande_cinema.model.Reservation;
import com.legrandecinema.le_grande_cinema.model.Seance;
import com.legrandecinema.le_grande_cinema.model.type.StatutReservation;
import com.legrandecinema.le_grande_cinema.repositories.PlaceSeanceRepository;
import com.legrandecinema.le_grande_cinema.repositories.ReservationRepository;
import com.legrandecinema.le_grande_cinema.repositories.SeanceRepository;
import com.legrandecinema.le_grande_cinema.service.policy.ReservationExpirationPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationExpirationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private PlaceSeanceRepository placeSeanceRepository;

    @Mock
    private SeanceRepository seanceRepository;

    private ReservationExpirationServiceImpl expirationService;

    @BeforeEach
    void setUp() {
        ReservationExpirationPolicy expirationPolicy =
                new ReservationExpirationPolicy(15);

        expirationService = new ReservationExpirationServiceImpl(
                reservationRepository,
                placeSeanceRepository,
                seanceRepository,
                expirationPolicy
        );
    }

    @Test
    void shouldCancelExpiredReservationAndReleaseSeats() {

        // creation: idSeance, réservation et placeSeance associées
        Integer idSeance = 1;

        Reservation reservation = new Reservation();
        reservation.setIdReservation(10);
        reservation.setStatut(StatutReservation.EN_ATTENTE);
        reservation.setDateReservation(
                LocalDateTime.now().minusMinutes(30)
        );

        PlaceSeance place = new PlaceSeance();
        place.setReservation(reservation);

        when(seanceRepository.findByIdForUpdate(idSeance))
                .thenReturn(Optional.of(new Seance()));

        when(reservationRepository.findBySeanceIdSeance(idSeance))
                .thenReturn(List.of(reservation));

        when(placeSeanceRepository.findByReservationIdReservation(10))
                .thenReturn(List.of(place));

        // on lance le service d'expiration
        expirationService.expirerReservationsDeLaSeance(idSeance);

        // la réservation devrait être annulée
        assertEquals(
                StatutReservation.ANNULEE,
                reservation.getStatut()
        );

        // la place devrait être libérée
        assertNull(place.getReservation());

        // Vérification des sauvegardes
        verify(placeSeanceRepository).saveAll(List.of(place));
        verify(reservationRepository).save(reservation);
    }

    @Test
    void shouldKeepRecentReservationAndItsSeats() {

        //Création d'un idSeance et d'une réservation
        Integer idSeance = 1;

        Reservation reservation = new Reservation();
        reservation.setIdReservation(20);
        reservation.setStatut(StatutReservation.EN_ATTENTE);
        reservation.setDateReservation(
                LocalDateTime.now().minusMinutes(5)
        );

        when(seanceRepository.findByIdForUpdate(idSeance))
                .thenReturn(Optional.of(new Seance()));

        when(reservationRepository.findBySeanceIdSeance(idSeance))
                .thenReturn(List.of(reservation));

        // on lance le service d'expiration
        expirationService.expirerReservationsDeLaSeance(idSeance);

        // la réservation devrait restee en attente
        assertEquals(
                StatutReservation.EN_ATTENTE,
                reservation.getStatut()
        );

        // Aucune modification en base
        verify(placeSeanceRepository, never()).saveAll(any());
        verify(reservationRepository, never()).save(any());
    }
}
