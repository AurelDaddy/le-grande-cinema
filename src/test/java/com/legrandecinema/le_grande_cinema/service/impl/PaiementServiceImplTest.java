package com.legrandecinema.le_grande_cinema.service.impl;

import com.legrandecinema.le_grande_cinema.model.Paiement;
import com.legrandecinema.le_grande_cinema.model.Reservation;
import com.legrandecinema.le_grande_cinema.model.type.StatutPaiement;
import com.legrandecinema.le_grande_cinema.model.type.StatutReservation;
import com.legrandecinema.le_grande_cinema.payment.PaymentGateway;
import com.legrandecinema.le_grande_cinema.repositories.PaiementRepository;
import com.legrandecinema.le_grande_cinema.repositories.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaiementServiceImplTest {

    @Test
    void shouldConfirmReservationWhenPaymentIsAccepted() {

        PaiementRepository paiementRepository = Mockito.mock(PaiementRepository.class);

        ReservationRepository reservationRepository = Mockito.mock(ReservationRepository.class);

        PaymentGateway paymentGateway = Mockito.mock(PaymentGateway.class);

        PaiementServiceImpl paiementService = new PaiementServiceImpl(
                paiementRepository,
                reservationRepository,
                paymentGateway);

        //On créé une réservation
        Reservation reservation = new Reservation();
        reservation.setStatut(StatutReservation.EN_ATTENTE);

        //On créé un paiement
        Paiement paiement = new Paiement();
        paiement.setMontant(new BigDecimal("10.00"));
        paiement.setReservation(reservation);

        //On mock le paymentGateway on retournant le statut ACCEPTE
        Mockito.when(
                paymentGateway.payer(paiement.getMontant())
                ).thenReturn(StatutPaiement.ACCEPTE);

        //On déclenche le paiement
        paiementService.effectuerPaiement(paiement);
        //On test que le statut de la réservation a bien changé
        assertEquals(StatutReservation.CONFIRMEE, reservation.getStatut());

        //On vérifie que PaiementServiceImpl a bien appelé paiementRepository.save
        //et reservationRepository.save
        Mockito.verify(paiementRepository).save(paiement);
        Mockito.verify(reservationRepository).save(reservation);
    }

    @Test
    void shouldNotConfirmReservationWhenPaymentIsRefused() {

        PaiementRepository paiementRepository = Mockito.mock(PaiementRepository.class);

        ReservationRepository reservationRepository = Mockito.mock(ReservationRepository.class);

        PaymentGateway paymentGateway = Mockito.mock(PaymentGateway.class);

        PaiementServiceImpl paiementService = new PaiementServiceImpl(
                paiementRepository,
                reservationRepository,
                paymentGateway);

        //On créé une réservation
        Reservation reservation = new Reservation();
        reservation.setStatut(StatutReservation.EN_ATTENTE);

        //On créé un paiement
        Paiement paiement = new Paiement();
        paiement.setMontant(new BigDecimal("10.00"));
        paiement.setReservation(reservation);

        //On mock le paymentGateway on retournant le statut REFUSE
        Mockito.when(
                paymentGateway.payer(paiement.getMontant())
        ).thenReturn(StatutPaiement.REFUSE);

        //On déclenche le paiement
        paiementService.effectuerPaiement(paiement);
        //On test que le statut de la réservation est toujours en attente
        // car le paiement n'est pas passé
        assertEquals(StatutReservation.EN_ATTENTE, reservation.getStatut());

        //On vérifie que PaiementServiceImpl appelle paiementRepository.save
        //mais pas reservationRepository.save
        Mockito.verify(paiementRepository).save(paiement);
        Mockito.verify(reservationRepository, Mockito.never()).save(reservation);

    }
}
