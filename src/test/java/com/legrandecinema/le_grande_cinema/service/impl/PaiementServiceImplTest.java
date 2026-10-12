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
import java.util.Optional;

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

        //On crée une réservation
        Reservation reservation = new Reservation();
        reservation.setIdReservation(1);
        reservation.setStatut(StatutReservation.EN_ATTENTE);

        BigDecimal montant = new BigDecimal("20.00");

        Mockito.when(reservationRepository.findById(1))
                .thenReturn(Optional.of(reservation));


        //On mock le paymentGateway on retournant le statut ACCEPTE
        Mockito.when(
                paymentGateway.payer(montant)
                ).thenReturn(StatutPaiement.ACCEPTE);

        Mockito.when(
                paiementRepository.save(Mockito.any(Paiement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //On déclenche le paiement
        Paiement paiement = paiementService.effectuerPaiement(1,
                montant
        );
        //On test que le statut de la réservation a bien changé
        assertEquals(StatutReservation.CONFIRMEE, reservation.getStatut());
        assertEquals(StatutPaiement.ACCEPTE, paiement.getStatut());
        assertEquals(montant, paiement.getMontant());
        assertEquals(reservation,  paiement.getReservation());

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


        //On crée une réservation
        Reservation reservation = new Reservation();
        reservation.setIdReservation(1);
        reservation.setStatut(StatutReservation.EN_ATTENTE);

        BigDecimal montant = new BigDecimal("20.00");

        Mockito.when(reservationRepository.findById(1))
                .thenReturn(Optional.of(reservation));



        //On mock le paymentGateway on retournant le statut REFUSE
        Mockito.when(
                paymentGateway.payer(montant)
        ).thenReturn(StatutPaiement.REFUSE);

        Mockito.when(paiementRepository.save(Mockito.any(Paiement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //On déclenche le paiement
        Paiement paiement = paiementService.effectuerPaiement(1,montant);

        //On test que le statut de la réservation est toujours en attente
        //et que le paiement a bien été refusé
        assertEquals(StatutReservation.EN_ATTENTE, reservation.getStatut());
        assertEquals(StatutPaiement.REFUSE, paiement.getStatut());

        //On vérifie que PaiementServiceImpl appelle paiementRepository.save
        //mais pas reservationRepository.save
        Mockito.verify(paiementRepository).save(paiement);
        Mockito.verify(reservationRepository, Mockito.never()).save(reservation);

    }
}
