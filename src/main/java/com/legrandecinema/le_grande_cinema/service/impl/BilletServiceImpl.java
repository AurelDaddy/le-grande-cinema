package com.legrandecinema.le_grande_cinema.service.impl;

import com.legrandecinema.le_grande_cinema.dto.BilletDto;
import com.legrandecinema.le_grande_cinema.exception.RessourceIntrouvableException;
import com.legrandecinema.le_grande_cinema.model.Paiement;
import com.legrandecinema.le_grande_cinema.model.Reservation;
import com.legrandecinema.le_grande_cinema.model.type.StatutPaiement;
import com.legrandecinema.le_grande_cinema.model.type.StatutReservation;
import com.legrandecinema.le_grande_cinema.repositories.ReservationRepository;
import com.legrandecinema.le_grande_cinema.service.BilletService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BilletServiceImpl implements BilletService {

    private final ReservationRepository reservationRepository;

    public BilletServiceImpl(
            ReservationRepository reservationRepository
    ) {
        this.reservationRepository = reservationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BilletDto> getBilletsUtilisateur(String email) {

        return reservationRepository
                .findByUtilisateurEmailAndStatut(
                        email,
                        StatutReservation.CONFIRMEE
                )
                .stream()
                .map(this::toBilletDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BilletDto getBilletByReservation(
            String email,
            Integer idReservation
    ) {

        Reservation reservation = reservationRepository
                .findById(idReservation)
                .orElseThrow(() ->
                        new RessourceIntrouvableException(
                                "Réservation introuvable"
                        )
                );

        if (!reservation
                .getUtilisateur()
                .getEmail()
                .equals(email)) {

            throw new IllegalStateException(
                    "Cette réservation n'appartient pas à l'utilisateur connecté"
            );
        }

        if (reservation.getStatut()
                != StatutReservation.CONFIRMEE) {

            throw new IllegalStateException(
                    "Cette réservation n'est pas encore confirmée elle ne possède donc pas encore de billet"
            );
        }

        return toBilletDto(reservation);
    }

    private BilletDto toBilletDto(
            Reservation reservation
    ) {

        Paiement paiementAccepte =
                reservation
                        .getPaiements()
                        .stream()
                        .filter(paiement ->
                                paiement.getStatut()
                                        == StatutPaiement.ACCEPTE
                        )
                        .findFirst()
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Aucun paiement accepté pour cette réservation"
                                )
                        );

        //Tri des placeSeances
        List<String> places =
                reservation
                        .getPlaceSeances()
                        .stream()
                        .sorted(
                                (placeSeance1, placeSeance2) -> {

                                    //comparaison d'abord des rangs
                                    int comparaisonRang =
                                            placeSeance1
                                                    .getPlace()
                                                    .getRang()
                                                    .compareTo(
                                                            placeSeance2
                                                                    .getPlace()
                                                                    .getRang()
                                                    );

                                    if (comparaisonRang != 0) {
                                        return comparaisonRang;
                                    }
                                    //Si les rangs sont identiques alors comparaison des numéros
                                    return Integer.compare(
                                            placeSeance1
                                                    .getPlace()
                                                    .getNumero(),
                                            placeSeance2
                                                    .getPlace()
                                                    .getNumero()
                                    );
                                }
                        )
                        .map(placeSeance ->
                                placeSeance
                                        .getPlace()
                                        .getRang()
                                        +
                                        placeSeance
                                                .getPlace()
                                                .getNumero()
                        )
                        .toList();

        return new BilletDto(
                reservation.getIdReservation(),
                reservation
                        .getSeance()
                        .getFilm()
                        .getTitre(),
                reservation
                        .getSeance()
                        .getDateHeure(),
                reservation
                        .getSeance()
                        .getVersion(),
                reservation
                        .getSeance()
                        .getSalle()
                        .getNumeroSalle(),
                places,
                reservation
                        .getUtilisateur()
                        .getPrenom(),
                reservation
                        .getUtilisateur()
                        .getNom(),
                paiementAccepte
                        .getReferencePaiement()
        );
    }
}