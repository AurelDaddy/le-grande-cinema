package com.legrandecinema.le_grande_cinema.service.impl;

import com.legrandecinema.le_grande_cinema.model.Paiement;
import com.legrandecinema.le_grande_cinema.model.Reservation;
import com.legrandecinema.le_grande_cinema.model.type.StatutPaiement;
import com.legrandecinema.le_grande_cinema.model.type.StatutReservation;
import com.legrandecinema.le_grande_cinema.payment.PaymentGateway;
import com.legrandecinema.le_grande_cinema.repositories.PaiementRepository;
import com.legrandecinema.le_grande_cinema.repositories.ReservationRepository;
import com.legrandecinema.le_grande_cinema.service.PaiementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaiementServiceImpl implements PaiementService {

    private final PaiementRepository paiementRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentGateway paymentGateway;

    public PaiementServiceImpl(PaiementRepository paiementRepository,
                               ReservationRepository reservationRepository,
                               PaymentGateway paymentGateway) {
        this.paiementRepository = paiementRepository;
        this.reservationRepository = reservationRepository;
        this.paymentGateway = paymentGateway;
    }

    @Override
    @Transactional //Sauvegarde du paiement et mise à jour de la réservation dans une même transaction
    public Paiement effectuerPaiement(Integer idReservation, BigDecimal montant) {

        Reservation reservation = reservationRepository.findById(idReservation).orElseThrow();

        Paiement paiement = new Paiement();

        paiement.setReservation(reservation);
        paiement.setMontant(montant);
        paiement.setDatePaiement(LocalDateTime.now());

        StatutPaiement statutPaiement = paymentGateway.payer(montant);
        paiement.setStatut(statutPaiement);
        Paiement paiementEnregistre = paiementRepository.save(paiement);

        //Si le statut du paiement est accepté alors on confirme la réservation
        if (statutPaiement == StatutPaiement.ACCEPTE){
           reservation.setStatut(StatutReservation.CONFIRMEE);
           reservationRepository.save(reservation);
        }
        return paiementEnregistre;
    }

    @Override
    public List<Paiement> getPaiementsByReservation(Integer idReservation) {
        return paiementRepository.findAllByReservationIdReservation(idReservation);
    }

}
