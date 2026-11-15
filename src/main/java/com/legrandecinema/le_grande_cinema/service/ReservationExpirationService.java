
package com.legrandecinema.le_grande_cinema.service;

public interface ReservationExpirationService {

    // Annule les réservations expirées et libère leurs places
    // pour une séance donnée.
    void expirerReservationsDeLaSeance(Integer idSeance);
}
