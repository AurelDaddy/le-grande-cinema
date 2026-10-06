package com.legrandecinema.le_grande_cinema.service.impl;

import com.legrandecinema.le_grande_cinema.model.Paiement;
import com.legrandecinema.le_grande_cinema.model.Reservation;
import com.legrandecinema.le_grande_cinema.model.type.StatutPaiement;
import com.legrandecinema.le_grande_cinema.model.type.StatutReservation;
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

    public PaiementServiceImpl(PaiementRepository paiementRepository, ReservationRepository reservationRepository) {
        this.paiementRepository = paiementRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    @Transactional //car on effectue 2 modifications liées
    public Paiement effectuerPaiement(Paiement paiement) {

        Paiement paiementEnregistre = paiementRepository.save(paiement);

        //Si le statut du paiement est accepté alors on récupère la réservation
        //du paiement et on change son statut en CONFIRMEE puis on enregistre
        if (paiement.getStatut() == StatutPaiement.ACCEPTE){
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
