package com.legrandecinema.le_grande_cinema.service.impl;

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

        //On crée 2 placesSéance auxquelles on affecte la même séance
        PlaceSeance placeSeance1 = new PlaceSeance();
        PlaceSeance placeSeance2 = new PlaceSeance();
        placeSeance1.setSeance(seance);
        placeSeance2.setSeance(seance);

        List<PlaceSeance> placesDisponibles = List.of(placeSeance1, placeSeance2);

        Mockito.when(utilisateurRepository.findById(1) // 1 -> id fictif de l'utilisateur nouvellement créé
        ).thenReturn(Optional.of(utilisateur));

        Mockito.when(seanceRepository.findById(1) // 1 id fictif de la séance nouvellement créée
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

        Reservation reservationTest = reservationService.createReservation(1, 1, 2);

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

        //On crée 2 placesSéance auxquelles on affecte la même séance
        PlaceSeance placeSeance1 = new PlaceSeance();
        PlaceSeance placeSeance2 = new PlaceSeance();
        placeSeance1.setSeance(seance);
        placeSeance2.setSeance(seance);

        List<PlaceSeance> placesDisponibles = List.of(placeSeance1, placeSeance2);

        Mockito.when(utilisateurRepository.findById(1) // 1 -> id fictif de l'utilisateur nouvellement créé
        ).thenReturn(Optional.of(utilisateur));

        Mockito.when(seanceRepository.findById(1) // 1 id fictif de la séance nouvellement créée
        ).thenReturn(Optional.of(seance));

        Mockito.when(
                placeSeanceRepository.findBySeanceIdSeanceAndReservationIsNull(
                        Mockito.eq(1), // on veut que ce soit cet id 1 précisemment
                        Mockito.any(Pageable.class) //par contre cela peut être n'importe quel pageable
                )
        ).thenReturn(placesDisponibles);

        //on vérifie que quand reservationService.createReservation est lancé, il envoie une exception car pas assez de places disponibles
        assertThrows(
                IllegalStateException.class, () -> reservationService.createReservation(1, 1, 3)
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
                IllegalArgumentException.class, () -> reservationService.createReservation(1, 1, 0)
        );

        Mockito.verifyNoInteractions(
                reservationRepository,
                seanceRepository,
                utilisateurRepository,
                placeSeanceRepository);

    }
}