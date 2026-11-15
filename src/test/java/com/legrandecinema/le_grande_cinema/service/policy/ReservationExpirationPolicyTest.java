
package com.legrandecinema.le_grande_cinema.service.policy;

import com.legrandecinema.le_grande_cinema.model.Reservation;
import com.legrandecinema.le_grande_cinema.model.type.StatutReservation;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ReservationExpirationPolicyTest {

    // Durée de validité de 15 minutes
    private final ReservationExpirationPolicy expirationPolicy =
            new ReservationExpirationPolicy(15);

    @Test
    void shouldNotExpireRecentReservation() {

        // réservation créée il y a 10 minutes
        Reservation reservation = new Reservation();
        reservation.setStatut(StatutReservation.EN_ATTENTE);
        reservation.setDateReservation(
                LocalDateTime.now().minusMinutes(10)
        );

        boolean resultat = expirationPolicy.estExpiree(reservation);

        // la réservation devrait toujours être valide
        assertFalse(resultat);
    }

    @Test
    void shouldExpireOldReservation() {

        // réservation créée il y a 30 minutes
        Reservation reservation = new Reservation();
        reservation.setStatut(StatutReservation.EN_ATTENTE);
        reservation.setDateReservation(
                LocalDateTime.now().minusMinutes(30)
        );

        boolean resultat = expirationPolicy.estExpiree(reservation);

        // la réservation devrait être expirée
        assertTrue(resultat);
    }

    @Test
    void shouldNotExpireConfirmedReservation() {

        // réservation confirmée il y a 30 minutes
        Reservation reservation = new Reservation();
        reservation.setStatut(StatutReservation.CONFIRMEE);
        reservation.setDateReservation(
                LocalDateTime.now().minusMinutes(30)
        );

        boolean resultat = expirationPolicy.estExpiree(reservation);

        // une réservation confirmée ne devrait pas expirer
        assertFalse(resultat);
    }

    @Test
    void shouldRejectMissingReservationDate() {

        // réservation en attente sans date
        Reservation reservation = new Reservation();
        reservation.setStatut(StatutReservation.EN_ATTENTE);

        // crée une exception car une date est obligatoire
        assertThrows(
                IllegalStateException.class,
                () -> expirationPolicy.estExpiree(reservation)
        );
    }

    @Test
    void shouldRejectInvalidExpirationDuration() {

        // Une durée nulle ou négative est interdite
        assertThrows(
                IllegalArgumentException.class,
                () -> new ReservationExpirationPolicy(0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new ReservationExpirationPolicy(-5)
        );
    }
}
