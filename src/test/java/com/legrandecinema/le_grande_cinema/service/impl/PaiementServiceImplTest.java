package com.legrandecinema.le_grande_cinema.service.impl;

import com.legrandecinema.le_grande_cinema.model.*;
import com.legrandecinema.le_grande_cinema.model.type.StatutPaiement;
import com.legrandecinema.le_grande_cinema.model.type.StatutReservation;
import com.legrandecinema.le_grande_cinema.payment.PaymentGateway;
import com.legrandecinema.le_grande_cinema.repositories.PaiementRepository;
import com.legrandecinema.le_grande_cinema.repositories.ReservationRepository;
import com.legrandecinema.le_grande_cinema.repositories.SeanceRepository;
import com.legrandecinema.le_grande_cinema.service.policy.ReservationExpirationPolicy;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class PaiementServiceImplTest {

    @Test
    void shouldConfirmReservationWhenPaymentIsAccepted() {

        PaiementRepository paiementRepository = Mockito.mock(PaiementRepository.class);

        ReservationRepository reservationRepository = Mockito.mock(ReservationRepository.class);

        PaymentGateway paymentGateway = Mockito.mock(PaymentGateway.class);

        ReservationExpirationPolicy expirationPolicy = new ReservationExpirationPolicy(15);

        SeanceRepository seanceRepository = Mockito.mock(SeanceRepository.class); {
        }



        PaiementServiceImpl paiementService = new PaiementServiceImpl(
                paiementRepository,
                reservationRepository,
                paymentGateway,
                expirationPolicy,
                seanceRepository);

        BigDecimal montant = BigDecimal.valueOf(20);

        //on crée un utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail("michel.dumont@test.fr");

        //on crée une séance
        Seance seance = new Seance();
        seance.setPrix(BigDecimal.valueOf(10));

        //on crée une liste de PlaceSeance
        PlaceSeance placeSeance1 = new PlaceSeance();
        placeSeance1.setSeance(seance);
        PlaceSeance placeSeance2 = new PlaceSeance();
        placeSeance2.setSeance(seance);
        List<PlaceSeance> placeSeances = List.of(placeSeance1, placeSeance2);


        //On crée une réservation
        Reservation reservation = new Reservation();
        reservation.setIdReservation(1);
        reservation.setPlaceSeances(placeSeances);
        reservation.setSeance(seance);
        reservation.setStatut(StatutReservation.EN_ATTENTE);
        reservation.setDateReservation(LocalDateTime.now().minusMinutes(5));
        reservation.setUtilisateur(utilisateur);


        Mockito.when(reservationRepository.findSeanceIdByReservationId(1))
                .thenReturn(Optional.of(10));

        Mockito.when(seanceRepository.findByIdForUpdate(10))
                .thenReturn(Optional.of(new Seance()));

        Mockito.when(reservationRepository.findByIdForUpdate(1))
                .thenReturn(Optional.of(reservation));



        //On mock le paymentGateway on retournant le statut ACCEPTE
        Mockito.when(
                paymentGateway.payer(montant)
                ).thenReturn(StatutPaiement.ACCEPTE);

        Mockito.when(
                paiementRepository.save(Mockito.any(Paiement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //On déclenche le paiement
        Paiement paiement = paiementService.effectuerPaiement("michel.dumont@test.fr", 1);
        //On test que le statut de la réservation a bien changé
        assertEquals(StatutReservation.CONFIRMEE, reservation.getStatut());
        assertEquals(StatutPaiement.ACCEPTE, paiement.getStatut());
        assertEquals(montant, paiement.getMontant());
        assertSame(reservation,  paiement.getReservation());

        //On vérifie que PaiementServiceImpl a bien appelé paiementRepository.save
        //et reservationRepository.save
        Mockito.verify(paiementRepository).save(paiement);
        Mockito.verify(reservationRepository).save(reservation);
    }

    @Test
    void shouldRefusePaymentWhenReservationDoesntBelongToUtilisateur() {
        PaiementRepository paiementRepository = Mockito.mock(PaiementRepository.class);
        ReservationRepository reservationRepository = Mockito.mock(ReservationRepository.class);
        PaymentGateway paymentGateway = Mockito.mock(PaymentGateway.class);
        ReservationExpirationPolicy expirationPolicy = new ReservationExpirationPolicy(15);
        SeanceRepository seanceRepository = Mockito.mock(SeanceRepository.class);

        PaiementServiceImpl paiementService = new PaiementServiceImpl(
                paiementRepository,
                reservationRepository,
                paymentGateway,
                expirationPolicy,
                seanceRepository);

        //on crée un utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail("michel.dumont@test.fr");

        //On crée une réservation
        Reservation reservation = new Reservation();
        reservation.setIdReservation(1);
        reservation.setStatut(StatutReservation.EN_ATTENTE);
        reservation.setDateReservation(LocalDateTime.now().minusMinutes(5));
        reservation.setUtilisateur(utilisateur);


        Mockito.when(reservationRepository.findSeanceIdByReservationId(1))
                .thenReturn(Optional.of(10));

        Mockito.when(seanceRepository.findByIdForUpdate(10))
                .thenReturn(Optional.of(new Seance()));

        Mockito.when(reservationRepository.findByIdForUpdate(1))
                .thenReturn(Optional.of(reservation));


        assertThrows(
                IllegalStateException.class,
                () -> paiementService.effectuerPaiement(
                        "autre@test.fr",
                        1)
        );

        Mockito.verify(paiementRepository, Mockito.never())
                .save(Mockito.any(Paiement.class));

        Mockito.verify(paymentGateway, Mockito.never())
                .payer(Mockito.any(BigDecimal.class));
    }


    @Test
    void shouldNotConfirmReservationWhenPaymentIsRefused() {

        PaiementRepository paiementRepository = Mockito.mock(PaiementRepository.class);

        ReservationRepository reservationRepository = Mockito.mock(ReservationRepository.class);

        PaymentGateway paymentGateway = Mockito.mock(PaymentGateway.class);

        ReservationExpirationPolicy expirationPolicy = new ReservationExpirationPolicy(15);

        SeanceRepository seanceRepository = Mockito.mock(SeanceRepository.class);

        PaiementServiceImpl paiementService = new PaiementServiceImpl(
                paiementRepository,
                reservationRepository,
                paymentGateway,
                expirationPolicy,
                seanceRepository);

        BigDecimal montant = BigDecimal.valueOf(20);

        //on crée un utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail("michel.dumont@test.fr");

        //on crée une séance
        Seance seance = new Seance();
        seance.setPrix(BigDecimal.valueOf(10));

        //on crée une liste de PlaceSeance
        PlaceSeance placeSeance1 = new PlaceSeance();
        placeSeance1.setSeance(seance);
        PlaceSeance placeSeance2 = new PlaceSeance();
        placeSeance2.setSeance(seance);
        List<PlaceSeance> placeSeances = List.of(placeSeance1, placeSeance2);


        //On crée une réservation
        Reservation reservation = new Reservation();
        reservation.setIdReservation(1);
        reservation.setPlaceSeances(placeSeances);
        reservation.setSeance(seance);
        reservation.setStatut(StatutReservation.EN_ATTENTE);
        reservation.setDateReservation(LocalDateTime.now().minusMinutes(5));
        reservation.setUtilisateur(utilisateur);



        Mockito.when(reservationRepository.findSeanceIdByReservationId(1))
                .thenReturn(Optional.of(10));

        Mockito.when(seanceRepository.findByIdForUpdate(10))
                .thenReturn(Optional.of(new Seance()));

        Mockito.when(reservationRepository.findByIdForUpdate(1))
                .thenReturn(Optional.of(reservation));




        //On mock le paymentGateway on retournant le statut REFUSE
        Mockito.when(
                paymentGateway.payer(montant)
        ).thenReturn(StatutPaiement.REFUSE);

        Mockito.when(paiementRepository.save(Mockito.any(Paiement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //On déclenche le paiement
        Paiement paiement = paiementService.effectuerPaiement("michel.dumont@test.fr", 1);

        //On teste que le statut de la réservation est toujours en attente
        //et que le paiement a bien été refusé
        assertEquals(StatutReservation.EN_ATTENTE, reservation.getStatut());
        assertEquals(StatutPaiement.REFUSE, paiement.getStatut());
        assertEquals(montant, paiement.getMontant());

        //On vérifie que PaiementServiceImpl appelle paiementRepository.save et paymentGateway
        //mais pas reservationRepository.save
        Mockito.verify(paymentGateway).payer(montant);
        Mockito.verify(paiementRepository).save(paiement);
        Mockito.verify(reservationRepository, Mockito.never()).save(reservation);

    }


    @Test
    void shouldAcceptPaymentOver999Point99Euros() {

        // ARRANGE : préparation des données de notre scénario

        PaiementRepository paiementRepository =
                Mockito.mock(PaiementRepository.class);

        ReservationRepository reservationRepository =
                Mockito.mock(ReservationRepository.class);

        PaymentGateway paymentGateway =
                Mockito.mock(PaymentGateway.class);

        ReservationExpirationPolicy expirationPolicy = new ReservationExpirationPolicy(15);

        SeanceRepository seanceRepository = Mockito.mock(SeanceRepository.class);

        PaiementServiceImpl paiementService = new PaiementServiceImpl(
                paiementRepository,
                reservationRepository,
                paymentGateway,
                expirationPolicy,
                seanceRepository
        );

        // Une séance coûte 12,50 € par place
        Seance seance = new Seance();
        seance.setPrix(new BigDecimal("12.50"));

        // Notre client souhaite réserver 100 places
        List<PlaceSeance> places = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            PlaceSeance place = new PlaceSeance();
            place.setSeance(seance);
            places.add(place);
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail("client@test.fr");

        Reservation reservation = new Reservation();
        reservation.setIdReservation(19);
        reservation.setUtilisateur(utilisateur);
        reservation.setSeance(seance);
        reservation.setPlaceSeances(places);
        reservation.setStatut(StatutReservation.EN_ATTENTE);
        reservation.setDateReservation(LocalDateTime.now().minusMinutes(5));

        BigDecimal montantAttendu = new BigDecimal("1250.00");

        // Simulation de la récupération de notre réservation
        Mockito.when(reservationRepository.findSeanceIdByReservationId(19))
                .thenReturn(Optional.of(10));

        Mockito.when(seanceRepository.findByIdForUpdate(10))
                .thenReturn(Optional.of(new Seance()));

        Mockito.when(reservationRepository.findByIdForUpdate(19))
                .thenReturn(Optional.of(reservation));


        // Simulation de l'acceptation du paiement
        Mockito.when(paymentGateway.payer(montantAttendu))
                .thenReturn(StatutPaiement.ACCEPTE);

        // Simulation de l'enregistrement du paiement
        Mockito.when(paiementRepository.save(Mockito.any(Paiement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Exécution de la méthode que nous souhaitons tester

        Paiement paiement = paiementService.effectuerPaiement(
                "client@test.fr",
                19
        );

        // Vérification des résultats obtenus

        assertEquals(montantAttendu, paiement.getMontant());
        assertEquals(StatutPaiement.ACCEPTE, paiement.getStatut());
        assertEquals(StatutReservation.CONFIRMEE, reservation.getStatut());

        // Vérification des interactions avec les dépendances
        Mockito.verify(paymentGateway).payer(montantAttendu);
        Mockito.verify(paiementRepository).save(paiement);
        Mockito.verify(reservationRepository).save(reservation);
    }


    @Test
    void shouldRefusePaymentWhenReservationHasExpired() {

        // Création des repositories mockés
        PaiementRepository paiementRepository =
                Mockito.mock(PaiementRepository.class);

        ReservationRepository reservationRepository =
                Mockito.mock(ReservationRepository.class);

        PaymentGateway paymentGateway =
                Mockito.mock(PaymentGateway.class);

        ReservationExpirationPolicy expirationPolicy = new ReservationExpirationPolicy(15);

        SeanceRepository seanceRepository = Mockito.mock(SeanceRepository.class);

        PaiementServiceImpl paiementService =
                new PaiementServiceImpl(
                        paiementRepository,
                        reservationRepository,
                        paymentGateway,
                        expirationPolicy,
                        seanceRepository
                );

        // Création de l'utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail("client@test.fr");

        // Création d'une séance à 12,50 €
        Seance seance = new Seance();
        seance.setPrix(new BigDecimal("12.50"));

        // Une place est associée à la réservation
        PlaceSeance place = new PlaceSeance();

        // Réservation créée il y a 30 minutes
        Reservation reservation = new Reservation();
        reservation.setIdReservation(1);
        reservation.setUtilisateur(utilisateur);
        reservation.setSeance(seance);
        reservation.setPlaceSeances(List.of(place));
        reservation.setStatut(StatutReservation.EN_ATTENTE);
        reservation.setDateReservation(
                LocalDateTime.now().minusMinutes(30)
        );

        // Le repository retrouve cette réservation

        Mockito.when(reservationRepository.findSeanceIdByReservationId(1))
                .thenReturn(Optional.of(10));

        Mockito.when(seanceRepository.findByIdForUpdate(10))
                .thenReturn(Optional.of(new Seance()));

        Mockito.when(reservationRepository.findByIdForUpdate(1))
                .thenReturn(Optional.of(reservation));


        // Le paiement doit être refusé
        assertThrows(
                IllegalStateException.class,
                () -> paiementService.effectuerPaiement(
                        "client@test.fr",
                        1
                )
        );

        // Le système ne doit jamais contacter le prestataire de paiement
        Mockito.verifyNoInteractions(paymentGateway);

        // Aucun paiement ne doit être enregistré
        Mockito.verify(paiementRepository, Mockito.never())
                .save(Mockito.any(Paiement.class));
    }


}
