
package com.legrandecinema.le_grande_cinema.service.impl;

import com.legrandecinema.le_grande_cinema.exception.RessourceIntrouvableException;
import com.legrandecinema.le_grande_cinema.model.PlaceSeance;
import com.legrandecinema.le_grande_cinema.model.Reservation;
import com.legrandecinema.le_grande_cinema.model.type.StatutReservation;
import com.legrandecinema.le_grande_cinema.repositories.PlaceSeanceRepository;
import com.legrandecinema.le_grande_cinema.repositories.ReservationRepository;
import com.legrandecinema.le_grande_cinema.repositories.SeanceRepository;
import com.legrandecinema.le_grande_cinema.service.ReservationExpirationService;
import com.legrandecinema.le_grande_cinema.service.policy.ReservationExpirationPolicy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReservationExpirationServiceImpl
        implements ReservationExpirationService {

    private final ReservationRepository reservationRepository;
    private final PlaceSeanceRepository placeSeanceRepository;
    private final SeanceRepository seanceRepository;
    private final ReservationExpirationPolicy expirationPolicy;

    public ReservationExpirationServiceImpl(
            ReservationRepository reservationRepository,
            PlaceSeanceRepository placeSeanceRepository,
            SeanceRepository seanceRepository,
            ReservationExpirationPolicy expirationPolicy) {

        this.reservationRepository = reservationRepository;
        this.placeSeanceRepository = placeSeanceRepository;
        this.seanceRepository = seanceRepository;
        this.expirationPolicy = expirationPolicy;
    }

    @Override
    @Transactional
    public void expirerReservationsDeLaSeance(Integer idSeance) {

        // Verrouillage de la séance pour coordonner les opérations
        // concurrentes de réservation et, à terme, de paiement.
        seanceRepository.findByIdForUpdate(idSeance)
                .orElseThrow(() ->
                        new RessourceIntrouvableException(
                                "Séance introuvable"
                        )
                );

        // Récupération des réservations de cette séance
        List<Reservation> reservations =
                reservationRepository.findBySeanceIdSeance(idSeance);

        for (Reservation reservation : reservations) {

            // On ignore les réservations encore valides,
            // confirmées ou déjà annulées.
            if (!expirationPolicy.estExpiree(reservation)) {
                continue;
            }

            // Recherche des places occupées par cette réservation
            List<PlaceSeance> places =
                    placeSeanceRepository.findByReservationIdReservation(
                            reservation.getIdReservation()
                    );

            // Libération des places
            for (PlaceSeance place : places) {
                place.setReservation(null);
            }

            placeSeanceRepository.saveAll(places);

            // Annulation de la réservation expirée
            reservation.setStatut(StatutReservation.ANNULEE);
            reservationRepository.save(reservation);
        }
    }
}
