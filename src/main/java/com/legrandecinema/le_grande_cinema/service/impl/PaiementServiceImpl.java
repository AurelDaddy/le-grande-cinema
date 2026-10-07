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
    public Paiement effectuerPaiement(Paiement paiement) {

        StatutPaiement statutPaiement = paymentGateway.payer(paiement.getMontant());
        paiement.setStatut(statutPaiement);
        Paiement paiementEnregistre = paiementRepository.save(paiement);

        //Si le statut du paiement est accepté alors on récupère la réservation
        //du paiement et on change son statut en CONFIRMEE puis on enregistre
        if (statutPaiement == StatutPaiement.ACCEPTE){
           Reservation reservation = paiement.getReservation();
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
