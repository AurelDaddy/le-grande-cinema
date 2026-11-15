package com.legrandecinema.le_grande_cinema.service.impl;

import com.legrandecinema.le_grande_cinema.exception.RessourceIntrouvableException;
import com.legrandecinema.le_grande_cinema.model.Paiement;
import com.legrandecinema.le_grande_cinema.model.Reservation;
import com.legrandecinema.le_grande_cinema.model.type.StatutPaiement;
import com.legrandecinema.le_grande_cinema.model.type.StatutReservation;
import com.legrandecinema.le_grande_cinema.payment.PaymentGateway;
import com.legrandecinema.le_grande_cinema.repositories.PaiementRepository;
import com.legrandecinema.le_grande_cinema.repositories.ReservationRepository;
import com.legrandecinema.le_grande_cinema.repositories.SeanceRepository;
import com.legrandecinema.le_grande_cinema.service.PaiementService;
import com.legrandecinema.le_grande_cinema.service.ReservationExpirationService;
import com.legrandecinema.le_grande_cinema.service.policy.ReservationExpirationPolicy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaiementServiceImpl implements PaiementService {

    private final PaiementRepository paiementRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentGateway paymentGateway;
    private final ReservationExpirationPolicy expirationPolicy;
    private final SeanceRepository seanceRepository;

    public PaiementServiceImpl(PaiementRepository paiementRepository,
                               ReservationRepository reservationRepository,
                               PaymentGateway paymentGateway,
                               ReservationExpirationPolicy expirationPolicy,
                               SeanceRepository seanceRepository) {
        this.paiementRepository = paiementRepository;
        this.reservationRepository = reservationRepository;
        this.paymentGateway = paymentGateway;
        this.expirationPolicy = expirationPolicy;
        this.seanceRepository = seanceRepository;
    }

    @Override
    @Transactional //Sauvegarde du paiement et mise à jour de la réservation dans une même transaction
    public Paiement effectuerPaiement(String email, Integer idReservation) {


// Récupération de l'identifiant de la séance associée à la réservation
        Integer idSeance = reservationRepository
                .findSeanceIdByReservationId(idReservation)
                .orElseThrow(() ->
                        new RessourceIntrouvableException(
                                "Réservation introuvable"
                        )
                );

// Verrouillage de la séance : les opérations concurrentes
// sur les réservations et les places devront attendre.
        seanceRepository
                .findByIdForUpdate(idSeance)
                .orElseThrow(() ->
                        new RessourceIntrouvableException(
                                "Séance introuvable"
                        )
                );

// Relecture de la réservation sous verrou.
// Cela permet de vérifier son état actuel après
// l'acquisition du verrou de la séance.
        Reservation reservation = reservationRepository
                .findByIdForUpdate(idReservation)
                .orElseThrow(() ->
                        new RessourceIntrouvableException(
                                "Réservation introuvable"
                        )
                );



        /*Reservation reservation = reservationRepository
                .findById(idReservation)
                .orElseThrow(() ->
                        new RessourceIntrouvableException(
                                "Réservation introuvable"
                        ));
        */

        if (!reservation.getUtilisateur().getEmail().equals(email)) {
            throw new IllegalStateException(
                    "L'email fourni n'est pas le même que celui de la réservation"
            );
        }

        if (reservation.getStatut() == StatutReservation.CONFIRMEE) {
            throw new IllegalStateException(
                    "Cette réservation a déjà été payée"
            );
        }

        if (reservation.getStatut() == StatutReservation.ANNULEE) {
            throw new IllegalStateException(
                    "Cette réservation a été annulée"
            );
        }

        // refuse le paiement si la reservation est expirée
        if (expirationPolicy.estExpiree(reservation)) {
            throw new IllegalStateException(
                    "Le délai de paiement de 15 minutes est dépassé"
            );
        }

        //ici on récupère le nombre de places désirées à partir de la réservation
        int nombrePlaces = reservation.getPlaceSeances().size();

        //qu'on multiplie par le prix de la séance
        BigDecimal montant = reservation
                .getSeance()
                .getPrix()
                .multiply(BigDecimal.valueOf(nombrePlaces));

        Paiement paiement = new Paiement();

        paiement.setReservation(reservation);
        paiement.setMontant(montant);
        paiement.setDatePaiement(LocalDateTime.now());

        //génération de referencePaiement
        String referencePaiement =
                "CINE" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8)
                                .toUpperCase();
        paiement.setReferencePaiement(referencePaiement);

        StatutPaiement statutPaiement = paymentGateway.payer(montant);
        paiement.setStatut(statutPaiement);
        Paiement paiementEnregistre = paiementRepository.save(paiement);

        //Si le statut du paiement est accepté alors on confirme la réservation
        if (statutPaiement == StatutPaiement.ACCEPTE) {
            reservation.setStatut(StatutReservation.CONFIRMEE);
            reservationRepository.save(reservation);
        }
        return paiementEnregistre;
    }

    @Override
    public List<Paiement> getPaiementsByReservation(Integer idReservation) {
        return paiementRepository.findAllByReservationIdReservation(idReservation);
    }

    @Override
    public Paiement getPaiementById(
            String email,
            int idPaiement
    ) {

        Paiement paiement = paiementRepository
                .findById(idPaiement)
                .orElseThrow(() ->
                        new RessourceIntrouvableException(
                                "Paiement introuvable"
                        )
                );

        if (!paiement
                .getReservation()
                .getUtilisateur()
                .getEmail()
                .equals(email)) {
            throw new IllegalStateException(
                    "Ce paiement n'appartient pas à l'utilisateur connecté"
            );
        }
        return paiement;
    }

}
