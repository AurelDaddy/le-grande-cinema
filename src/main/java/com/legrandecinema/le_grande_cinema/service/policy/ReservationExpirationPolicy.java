
package com.legrandecinema.le_grande_cinema.service.policy;

import com.legrandecinema.le_grande_cinema.model.Reservation;
import com.legrandecinema.le_grande_cinema.model.type.StatutReservation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ReservationExpirationPolicy {

    private final long expirationMinutes;

    public ReservationExpirationPolicy(
            @Value("${cinema.reservation.expiration-minutes}")
            long expirationMinutes) {

        if (expirationMinutes <= 0) {
            throw new IllegalArgumentException(
                    "La durée d'expiration doit être positive"
            );
        }

        this.expirationMinutes = expirationMinutes;
    }

    public boolean estExpiree(Reservation reservation) {

        // Seules les réservations en attente peuvent expirer.
        if (reservation.getStatut() != StatutReservation.EN_ATTENTE) {
            return false;
        }

        // Une réservation en attente doit posséder une date de création.
        if (reservation.getDateReservation() == null) {
            throw new IllegalStateException(
                    "Date de réservation manquante"
            );
        }

        LocalDateTime dateExpiration = reservation
                .getDateReservation()
                .plusMinutes(expirationMinutes);

        // Expirée lorsque la date limite est atteinte ou dépassée.
        return !dateExpiration.isAfter(LocalDateTime.now());
    }
}
