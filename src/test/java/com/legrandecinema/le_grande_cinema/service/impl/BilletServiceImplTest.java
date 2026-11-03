package com.legrandecinema.le_grande_cinema.service.impl;

import com.legrandecinema.le_grande_cinema.dto.BilletDto;
import com.legrandecinema.le_grande_cinema.model.*;
import com.legrandecinema.le_grande_cinema.model.type.StatutPaiement;
import com.legrandecinema.le_grande_cinema.model.type.StatutReservation;
import com.legrandecinema.le_grande_cinema.repositories.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


//ici on fait du pur mockito sans démarrer Spring
@ExtendWith(MockitoExtension.class)
class BilletServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;

    private BilletServiceImpl billetService;

    @BeforeEach
    void setUp() {
        billetService =
                new BilletServiceImpl(reservationRepository);
    }

    @Test
    void shouldReturnLesBilletsConfirmesDeUtilisateur() {

        Reservation reservation =
                creerReservationConfirmee();

        when(
                reservationRepository
                        .findByUtilisateurEmailAndStatut(
                                "client@test.fr",
                                StatutReservation.CONFIRMEE
                        )
        ).thenReturn(List.of(reservation));

        List<BilletDto> billets =
                billetService.getBilletsUtilisateur(
                        "client@test.fr"
                );

        assertEquals(1, billets.size());

        BilletDto billet = billets.get(0);

        assertEquals(
                reservation.getIdReservation(),
                billet.idReservation()
        );

        assertEquals(
                reservation.getSeance()
                        .getFilm()
                        .getTitre(),
                billet.titreFilm()
        );

        assertEquals(
                List.of("A2", "A10", "B1"),
                billet.places()
        );

        verify(reservationRepository)
                .findByUtilisateurEmailAndStatut(
                        "client@test.fr",
                        StatutReservation.CONFIRMEE
                );
    }

    @Test
    void shouldRefuseBilletAutreUtilisateur() {

        Reservation reservation =
                creerReservationConfirmee();

        when(
                reservationRepository.findById(1)
        ).thenReturn(
                Optional.of(reservation)
        );

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                billetService
                                        .getBilletByReservation(
                                                "autre@test.fr",
                                                1
                                        )
                );

        assertEquals(
                "Cette réservation n'appartient pas à l'utilisateur connecté",
                exception.getMessage()
        );
    }

    @Test
    void shouldRefuseReservationNonConfirmee() {

        Reservation reservation =
                creerReservationConfirmee();

        reservation.setStatut(
                StatutReservation.EN_ATTENTE
        );

        when(
                reservationRepository.findById(1)
        ).thenReturn(
                Optional.of(reservation)
        );

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                billetService
                                        .getBilletByReservation(
                                                "client@test.fr",
                                                1
                                        )
                );

        assertEquals(
                "Cette réservation n'est pas encore confirmée elle ne possède donc pas encore de billet",
                exception.getMessage()
        );
    }

    @Test
    void shouldRefuseReservationSansPaiementAccepte() {

        Reservation reservation =
                creerReservationConfirmee();

        Paiement paiementRefuse =
                new Paiement();

        paiementRefuse.setStatut(
                StatutPaiement.REFUSE
        );

        paiementRefuse.setReservation(
                reservation
        );

        reservation.setPaiements(
                List.of(paiementRefuse)
        );

        when(
                reservationRepository.findById(1)
        ).thenReturn(
                Optional.of(reservation)
        );

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                billetService
                                        .getBilletByReservation(
                                                "client@test.fr",
                                                1
                                        )
                );

        assertEquals(
                "Aucun paiement accepté pour cette réservation",
                exception.getMessage()
        );
    }

    @Test
    void shouldCreerBilletAvecPlacesTrieesEtReferencePaiement() {

        Reservation reservation =
                creerReservationConfirmee();

        when(
                reservationRepository.findById(1)
        ).thenReturn(
                Optional.of(reservation)
        );

        BilletDto billet =
                billetService
                        .getBilletByReservation(
                                "client@test.fr",
                                1
                        );

        assertEquals(
                List.of(
                        "A2",
                        "A10",
                        "B1"
                ),
                billet.places()
        );

        assertEquals(
                "CINE12345678",
                billet.referencePaiement()
        );

        assertEquals(
                "Inception",
                billet.titreFilm()
        );

        assertEquals(
                3,
                billet.numeroSalle()
        );
    }

    private Reservation creerReservationConfirmee() {

        Utilisateur utilisateur =
                new Utilisateur();

        utilisateur.setEmail(
                "client@test.fr"
        );

        utilisateur.setPrenom(
                "Jean"
        );

        utilisateur.setNom(
                "Dupont"
        );

        Film film =
                new Film();

        film.setTitre(
                "Inception"
        );

        Salle salle =
                new Salle();

        salle.setNumeroSalle(
                3
        );

        Seance seance =
                new Seance();

        seance.setFilm(
                film
        );

        seance.setSalle(
                salle
        );

        seance.setVersion(
                "VOSTFR"
        );

        seance.setDateHeure(
                LocalDateTime.of(
                        2026,
                        10,
                        18,
                        20,
                        30
                )
        );

        seance.setPrix(
                new BigDecimal("12.00")
        );

        Reservation reservation =
                new Reservation();

        reservation.setIdReservation(
                1
        );

        reservation.setUtilisateur(
                utilisateur
        );

        reservation.setSeance(
                seance
        );

        reservation.setStatut(
                StatutReservation.CONFIRMEE
        );

        Paiement paiement =
                new Paiement();

        paiement.setStatut(
                StatutPaiement.ACCEPTE
        );

        paiement.setReferencePaiement(
                "CINE12345678"
        );

        paiement.setReservation(
                reservation
        );

        reservation.setPaiements(
                List.of(paiement)
        );

        PlaceSeance placeB1 =
                creerPlaceSeance(
                        reservation,
                        seance,
                        "B",
                        1
                );

        PlaceSeance placeA10 =
                creerPlaceSeance(
                        reservation,
                        seance,
                        "A",
                        10
                );

        PlaceSeance placeA2 =
                creerPlaceSeance(
                        reservation,
                        seance,
                        "A",
                        2
                );

        reservation.setPlaceSeances(
                List.of(
                        placeB1,
                        placeA10,
                        placeA2
                )
        );

        return reservation;
    }

    private PlaceSeance creerPlaceSeance(
            Reservation reservation,
            Seance seance,
            String rang,
            int numero
    ) {

        Place place =
                new Place();

        place.setRang(
                rang
        );

        place.setNumero(
                numero
        );

        PlaceSeance placeSeance =
                new PlaceSeance();

        placeSeance.setReservation(
                reservation
        );

        placeSeance.setSeance(
                seance
        );

        placeSeance.setPlace(
                place
        );

        return placeSeance;
    }
}