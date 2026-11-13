package com.legrandecinema.le_grande_cinema.service.impl;

import com.legrandecinema.le_grande_cinema.exception.RessourceIntrouvableException;
import com.legrandecinema.le_grande_cinema.model.PlaceSeance;
import com.legrandecinema.le_grande_cinema.model.Reservation;
import com.legrandecinema.le_grande_cinema.model.Seance;
import com.legrandecinema.le_grande_cinema.model.Utilisateur;
import com.legrandecinema.le_grande_cinema.model.type.StatutReservation;
import com.legrandecinema.le_grande_cinema.repositories.PlaceSeanceRepository;
import com.legrandecinema.le_grande_cinema.repositories.ReservationRepository;
import com.legrandecinema.le_grande_cinema.repositories.SeanceRepository;
import com.legrandecinema.le_grande_cinema.repositories.UtilisateurRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReservationServiceImplTest {

    @Test
    void shouldCreateReservationWhenEnoughSeatAreAvailable() {
        //On mock les repository
        ReservationRepository reservationRepository = Mockito.mock(ReservationRepository.class);

        SeanceRepository seanceRepository = Mockito.mock(SeanceRepository.class);

        UtilisateurRepository utilisateurRepository = Mockito.mock(UtilisateurRepository.class);

        PlaceSeanceRepository placeSeanceRepository = Mockito.mock(PlaceSeanceRepository.class);

        //On construit ReservationService
        ReservationServiceImpl reservationService = new ReservationServiceImpl(
                reservationRepository,
                seanceRepository,
                utilisateurRepository,
                placeSeanceRepository);


        //On crée un utilisateur
        Utilisateur utilisateur = new Utilisateur();

        //On crée une séance
        Seance seance = new Seance();
        seance.setDateHeure(LocalDateTime.now().plusDays(1));

        //On crée 2 placesSéance auxquelles on affecte la même séance
        PlaceSeance placeSeance1 = new PlaceSeance();
        PlaceSeance placeSeance2 = new PlaceSeance();
        placeSeance1.setSeance(seance);
        placeSeance2.setSeance(seance);

        List<PlaceSeance> placesDisponibles = List.of(placeSeance1, placeSeance2);

        Mockito.when(utilisateurRepository.findByEmail("test@test.fr")
        ).thenReturn(Optional.of(utilisateur));

        Mockito.when(seanceRepository.findByIdForUpdate(1) // 1 id fictif de la séance nouvellement créée (mockée)
        ).thenReturn(Optional.of(seance));

        Mockito.when(
                placeSeanceRepository.findBySeanceIdSeanceAndReservationIsNull(
                        Mockito.eq(1), // on veut que ce soit cet id 1 précisemment
                        Mockito.any(Pageable.class) //par contre cela peut être n'importe quel pageable
                )
        ).thenReturn(placesDisponibles);

        Mockito.when(
                reservationRepository.save(Mockito.any(Reservation.class))
        ).thenAnswer(invocation -> invocation.getArgument(0)); //thenAnswer car on simule un save qui retourne l’entité qu’on vient de lui passer.

        Reservation reservationTest = reservationService.createReservation("test@test.fr", 1, 2);

        assertEquals(
                StatutReservation.EN_ATTENTE,
                reservationTest.getStatut()
        );

        assertEquals(
                reservationTest,
                placeSeance1.getReservation()
        );

        assertEquals(
                reservationTest,
                placeSeance2.getReservation()
        );

        //vérification que les PlaceSeance modifiées sont bien sauvegardées
        Mockito.verify(placeSeanceRepository).saveAll(placesDisponibles);

    }

    @Test
    void shouldThrowExceptionWhenNotEnoughSeatAreAvailable() {
        //On mock les repository
        ReservationRepository reservationRepository = Mockito.mock(ReservationRepository.class);

        SeanceRepository seanceRepository = Mockito.mock(SeanceRepository.class);

        UtilisateurRepository utilisateurRepository = Mockito.mock(UtilisateurRepository.class);

        PlaceSeanceRepository placeSeanceRepository = Mockito.mock(PlaceSeanceRepository.class);


        ReservationServiceImpl reservationService = new ReservationServiceImpl(
                reservationRepository,
                seanceRepository,
                utilisateurRepository,
                placeSeanceRepository);


        Utilisateur utilisateur = new Utilisateur();
        Seance seance = new Seance();
        seance.setDateHeure(LocalDateTime.now().plusDays(1));

        //On crée 2 placesSéance auxquelles on affecte la même séance
        PlaceSeance placeSeance1 = new PlaceSeance();
        PlaceSeance placeSeance2 = new PlaceSeance();
        placeSeance1.setSeance(seance);
        placeSeance2.setSeance(seance);

        List<PlaceSeance> placesDisponibles = List.of(placeSeance1, placeSeance2);

        Mockito.when(utilisateurRepository.findByEmail("test@test.fr")
        ).thenReturn(Optional.of(utilisateur));

        Mockito.when(seanceRepository.findByIdForUpdate(1) // 1 id fictif de la séance nouvellement créée
        ).thenReturn(Optional.of(seance));

        Mockito.when(
                placeSeanceRepository.findBySeanceIdSeanceAndReservationIsNull(
                        Mockito.eq(1), // on veut que ce soit cet id 1 précisemment
                        Mockito.any(Pageable.class) //par contre cela peut être n'importe quel pageable
                )
        ).thenReturn(placesDisponibles);

        //on vérifie que quand reservationService.createReservation est lancé, il envoie une exception car pas assez de places disponibles
        assertThrows(
                IllegalStateException.class, () -> reservationService.createReservation("test@test.fr", 1, 3)
        );

        //on vérifie que reservationRepository ne save pas la reservation
        Mockito.verify(
                reservationRepository, Mockito.never()).save(Mockito.any(Reservation.class));

        //on vérifie également que placeSeanceRepository n'essaie pas d'enregistrer les placeSeances
        Mockito.verify(
                placeSeanceRepository, Mockito.never()).saveAll(Mockito.anyList());

    }

    @Test
    void shouldThrowExceptionWhenZeroSeatsAreAsked() {
        //On mock les repository
        ReservationRepository reservationRepository = Mockito.mock(ReservationRepository.class);

        SeanceRepository seanceRepository = Mockito.mock(SeanceRepository.class);

        UtilisateurRepository utilisateurRepository = Mockito.mock(UtilisateurRepository.class);

        PlaceSeanceRepository placeSeanceRepository = Mockito.mock(PlaceSeanceRepository.class);


        ReservationServiceImpl reservationService = new ReservationServiceImpl(
                reservationRepository,
                seanceRepository,
                utilisateurRepository,
                placeSeanceRepository);

        //on vérifie que quand reservationService.createReservation est lancé, il envoie une exception car aucune place n'est demandée
        assertThrows(
                IllegalArgumentException.class, () -> reservationService.createReservation("test@test.fr", 1, 0)
        );

        Mockito.verifyNoInteractions(
                reservationRepository,
                seanceRepository,
                utilisateurRepository,
                placeSeanceRepository);

    }

    @Test
    void shouldThrowExceptionWhenSeanceIsPassed() {

        ReservationRepository reservationRepository = Mockito.mock(ReservationRepository.class);

        SeanceRepository seanceRepository = Mockito.mock(SeanceRepository.class);

        UtilisateurRepository utilisateurRepository = Mockito.mock(UtilisateurRepository.class);

        PlaceSeanceRepository placeSeanceRepository = Mockito.mock(PlaceSeanceRepository.class);


        ReservationServiceImpl reservationService = new ReservationServiceImpl(
                reservationRepository,
                seanceRepository,
                utilisateurRepository,
                placeSeanceRepository);

        Utilisateur utilisateur = new Utilisateur();
        Seance seancePassee = new Seance();
        seancePassee.setDateHeure(LocalDateTime.now().minusDays(1));

        Mockito.when(utilisateurRepository.findByEmail("test@test.fr"))
                        .thenReturn(Optional.of(utilisateur));

        Mockito.when(seanceRepository.findByIdForUpdate(1)
        ).thenReturn(Optional.of(seancePassee));

        //on vérifie que quand reservationService.createReservation est lancé, il envoie une exception car la séance est passée
        assertThrows(
                IllegalStateException.class, () -> reservationService.createReservation("test@test.fr", 1, 1)
        );

        //on vérifie que reservationRepository ne save pas la reservation
        Mockito.verify(
                reservationRepository, Mockito.never()).save(Mockito.any(Reservation.class));

    }

    @Test
    void shouldGetReservationById() {

        // création des repositories mockés
        ReservationRepository reservationRepository =
                Mockito.mock(ReservationRepository.class);

        SeanceRepository seanceRepository =
                Mockito.mock(SeanceRepository.class);

        UtilisateurRepository utilisateurRepository =
                Mockito.mock(UtilisateurRepository.class);

        PlaceSeanceRepository placeSeanceRepository =
                Mockito.mock(PlaceSeanceRepository.class);

        ReservationServiceImpl reservationService = new ReservationServiceImpl(
                reservationRepository,
                seanceRepository,
                utilisateurRepository,
                placeSeanceRepository
        );

        Reservation reservation = new Reservation();

        Mockito.when(reservationRepository.findById(1))
                .thenReturn(Optional.of(reservation));

        // appel de la méthode à tester
        Optional<Reservation> resultat =
                reservationService.getReservationById(1);

        // vérification du résultat
        assertEquals(Optional.of(reservation), resultat);

        Mockito.verify(reservationRepository).findById(1);
    }

    @Test
    void shouldGetConfirmedReservations() {

        ReservationRepository reservationRepository =
                Mockito.mock(ReservationRepository.class);

        SeanceRepository seanceRepository =
                Mockito.mock(SeanceRepository.class);

        UtilisateurRepository utilisateurRepository =
                Mockito.mock(UtilisateurRepository.class);

        PlaceSeanceRepository placeSeanceRepository =
                Mockito.mock(PlaceSeanceRepository.class);

        ReservationServiceImpl reservationService = new ReservationServiceImpl(
                reservationRepository,
                seanceRepository,
                utilisateurRepository,
                placeSeanceRepository
        );

        Reservation reservation = new Reservation();
        reservation.setStatut(StatutReservation.CONFIRMEE);

        List<Reservation> reservations = List.of(reservation);

        Mockito.when(reservationRepository.findByStatut(
                StatutReservation.CONFIRMEE
        )).thenReturn(reservations);

        List<Reservation> resultat =
                reservationService.getReservationsConfirmees();

        assertEquals(1, resultat.size());
        assertEquals(reservation, resultat.get(0));

        Mockito.verify(reservationRepository)
                .findByStatut(StatutReservation.CONFIRMEE);
    }

    @Test
    void shouldGetReservationsBySeance() {

        ReservationRepository reservationRepository =
                Mockito.mock(ReservationRepository.class);

        SeanceRepository seanceRepository =
                Mockito.mock(SeanceRepository.class);

        UtilisateurRepository utilisateurRepository =
                Mockito.mock(UtilisateurRepository.class);

        PlaceSeanceRepository placeSeanceRepository =
                Mockito.mock(PlaceSeanceRepository.class);

        ReservationServiceImpl reservationService = new ReservationServiceImpl(
                reservationRepository,
                seanceRepository,
                utilisateurRepository,
                placeSeanceRepository
        );

        Reservation reservation = new Reservation();
        List<Reservation> reservations = List.of(reservation);

        Mockito.when(reservationRepository.findBySeanceIdSeance(1))
                .thenReturn(reservations);

        // ACT
        List<Reservation> resultat =
                reservationService.getReservationsBySeance(1);

        // ASSERT
        assertEquals(1, resultat.size());
        assertEquals(reservation, resultat.get(0));

        Mockito.verify(reservationRepository)
                .findBySeanceIdSeance(1);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {

        // ARRANGE
        ReservationRepository reservationRepository =
                Mockito.mock(ReservationRepository.class);
        SeanceRepository seanceRepository =
                Mockito.mock(SeanceRepository.class);
        UtilisateurRepository utilisateurRepository =
                Mockito.mock(UtilisateurRepository.class);
        PlaceSeanceRepository placeSeanceRepository =
                Mockito.mock(PlaceSeanceRepository.class);

        ReservationServiceImpl reservationService =
                new ReservationServiceImpl(
                        reservationRepository,
                        seanceRepository,
                        utilisateurRepository,
                        placeSeanceRepository
                );

        // La séance existe
        Seance seance = new Seance();
        seance.setDateHeure(LocalDateTime.now().plusDays(1));

        Mockito.when(seanceRepository.findByIdForUpdate(1))
                .thenReturn(Optional.of(seance));

        // Mais l'utilisateur n'existe pas
        Mockito.when(utilisateurRepository.findByEmail("inconnu@test.fr"))
                .thenReturn(Optional.empty());

        assertThrows(
                RessourceIntrouvableException.class,
                () -> reservationService.createReservation(
                        "inconnu@test.fr", 1, 1
                )
        );

        // Aucune réservation ne doit être enregistrée
        Mockito.verify(reservationRepository, Mockito.never())
                .save(Mockito.any(Reservation.class));
    }

    @Test
    void shouldThrowExceptionWhenSeanceDoesNotExist() {

        // ARRANGE
        ReservationRepository reservationRepository =
                Mockito.mock(ReservationRepository.class);
        SeanceRepository seanceRepository =
                Mockito.mock(SeanceRepository.class);
        UtilisateurRepository utilisateurRepository =
                Mockito.mock(UtilisateurRepository.class);
        PlaceSeanceRepository placeSeanceRepository =
                Mockito.mock(PlaceSeanceRepository.class);

        ReservationServiceImpl reservationService =
                new ReservationServiceImpl(
                        reservationRepository,
                        seanceRepository,
                        utilisateurRepository,
                        placeSeanceRepository
                );

        // La séance demandée n'existe pas
        Mockito.when(seanceRepository.findByIdForUpdate(999))
                .thenReturn(Optional.empty());

        assertThrows(
                RessourceIntrouvableException.class,
                () -> reservationService.createReservation(
                        "client@test.fr", 999, 1
                )
        );

        // Aucun utilisateur ne doit être recherché inutilement
        Mockito.verifyNoInteractions(utilisateurRepository);

        // Aucune réservation ne doit être enregistrée
        Mockito.verify(reservationRepository, Mockito.never())
                .save(Mockito.any(Reservation.class));
    }



}