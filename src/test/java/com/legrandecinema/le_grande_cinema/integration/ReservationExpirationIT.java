
package com.legrandecinema.le_grande_cinema.integration;

import com.legrandecinema.le_grande_cinema.model.*;
import com.legrandecinema.le_grande_cinema.model.type.StatutPaiement;
import com.legrandecinema.le_grande_cinema.model.type.StatutReservation;
import com.legrandecinema.le_grande_cinema.model.type.TypeUtilisateur;
import com.legrandecinema.le_grande_cinema.service.PaiementService;
import com.legrandecinema.le_grande_cinema.service.ReservationExpirationService;
import com.legrandecinema.le_grande_cinema.service.ReservationService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("integration-mysql")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ReservationExpirationIT {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PaiementService paiementService;

    @Autowired
    private ReservationExpirationService expirationService;

    private Integer idSeance;

    @BeforeEach
    void preparerDonnees() {

        // Création de données fictives dans MySQL
        idSeance = transactionTemplate.execute(status -> {

            creerUtilisateur("expiration-a@test.fr");
            creerUtilisateur("expiration-b@test.fr");

            Film film = new Film();
            film.setTitre("Les Bronzés font du skis");
            film.setDureeMinute(100);
            film.setAnneeSortie(1979);
            film.setGenre("Comédie");
            film.setRealisateur("Patrice Leconte");
            film.setSynopsis("C'est marrant");
            film.setUrlAffiche("https://example.com/film.jpg");
            entityManager.persist(film);

            Salle salle = new Salle();
            salle.setNumeroSalle(99);
            entityManager.persist(salle);

            Seance seance = new Seance();
            seance.setFilm(film);
            seance.setSalle(salle);
            seance.setDateHeure(LocalDateTime.now().plusDays(7));
            seance.setVersion("VF");
            seance.setPrix(new BigDecimal("12.50"));
            entityManager.persist(seance);

            Place place = new Place();
            place.setSalle(salle);
            place.setRang("A");
            place.setNumero(1);
            entityManager.persist(place);

            PlaceSeance placeSeance = new PlaceSeance();
            placeSeance.setPlace(place);
            placeSeance.setSeance(seance);
            placeSeance.setReservation(null);
            entityManager.persist(placeSeance);

            entityManager.flush();

            return seance.getIdSeance();
        });
    }

    private void creerUtilisateur(String email) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setPrenom("Jean-Claude");
        utilisateur.setNom("Duss");
        utilisateur.setEmail(email);
        utilisateur.setMotDePasse("mot-de-passe");
        utilisateur.setTypeUtilisateur(TypeUtilisateur.CLIENT);
        entityManager.persist(utilisateur);
    }

    @Test
    void shouldReleaseSeatsFromExpiredReservation() {

        // Le client A réserve la dernière place
        Reservation reservationA =
                reservationService.createReservation(
                        "expiration-a@test.fr",
                        idSeance,
                        1
                );

        assertEquals(
                StatutReservation.EN_ATTENTE,
                reservationA.getStatut()
        );

        // Simulation d'une réservation créée il y a 30 minutes
        jdbcTemplate.update(
                "UPDATE reservation SET date_reservation = ? WHERE id_reservation = ?",
                Timestamp.valueOf(LocalDateTime.now().minusMinutes(30)),
                reservationA.getIdReservation()
        );

        // Vérification : la place est toujours occupée
        Long placesDisponiblesAvant = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM place_seance
                WHERE id_seance = ?
                  AND id_reservation IS NULL
                """,
                Long.class,
                idSeance
        );

        assertEquals(0L, placesDisponiblesAvant);

        // Le client B essaie de réserver la dernière place.
        Reservation reservationB = assertDoesNotThrow(
                () -> reservationService.createReservation(
                        "expiration-b@test.fr",
                        idSeance,
                        1
                )
        );

        // L'ancienne réservation doit être annulée
        String statutReservationA = jdbcTemplate.queryForObject(
                "SELECT statut FROM reservation WHERE id_reservation = ?",
                String.class,
                reservationA.getIdReservation()
        );

        assertEquals("ANNULEE", statutReservationA);

        // La place doit désormais appartenir à la réservation B
        Integer reservationDeLaPlace = jdbcTemplate.queryForObject(
                """
                SELECT id_reservation
                FROM place_seance
                WHERE id_seance = ?
                """,
                Integer.class,
                idSeance
        );

        assertEquals(reservationB.getIdReservation(), reservationDeLaPlace);
    }


    @Test
    void shouldPreventPaymentWhileExpiringReservation() throws Exception {

        // Le client A réserve la dernière place
        Reservation reservation = reservationService.createReservation(
                "expiration-a@test.fr",
                idSeance,
                1
        );

        Integer idReservation = reservation.getIdReservation();

        // On simule une réservation créée il y a 30 minutes
        jdbcTemplate.update(
                """
                UPDATE reservation
                SET date_reservation = ?
                WHERE id_reservation = ?
                """,
                Timestamp.valueOf(LocalDateTime.now().minusMinutes(30)),
                idReservation
        );

        ExecutorService executor = Executors.newFixedThreadPool(2);

        CountDownLatch pret = new CountDownLatch(2);
        CountDownLatch depart = new CountDownLatch(1);

        try {

            // Transaction A : tentative de paiement
            Future<Boolean> paiementRefuse = executor.submit(() -> {

                pret.countDown();
                depart.await();

                try {
                    paiementService.effectuerPaiement(
                            "expiration-a@test.fr",
                            idReservation
                    );

                    return false;

                } catch (IllegalStateException exception) {
                    // Un paiement expiré doit être refusé
                    return true;
                }
            });

            // Transaction B : expiration de la réservation
            Future<?> expiration = executor.submit(() -> {

                pret.countDown();
                depart.await();

                expirationService.expirerReservationsDeLaSeance(idSeance);

                return null;
            });

            // On attend que les deux opérations soient prêtes
            assertTrue(pret.await(10, TimeUnit.SECONDS));

            // On les lance simultanément
            depart.countDown();

            assertTrue(
                    paiementRefuse.get(20, TimeUnit.SECONDS),
                    "Le paiement expiré doit être refusé"
            );

            expiration.get(20, TimeUnit.SECONDS);

            // La réservation devrait être annulée
            String statut = jdbcTemplate.queryForObject(
                    """
                    SELECT statut
                    FROM reservation
                    WHERE id_reservation = ?
                    """,
                    String.class,
                    idReservation
            );

            assertEquals("ANNULEE", statut);

            // Sa place devrait être disponible
            Long placesDisponibles = jdbcTemplate.queryForObject(
                    """
                    SELECT COUNT(*)
                    FROM place_seance
                    WHERE id_seance = ?
                    AND id_reservation IS NULL
                    """,
                    Long.class,
                    idSeance
            );

            assertEquals(1L, placesDisponibles);

            // Aucun paiement ne devrait être enregistré
            Long nombrePaiements = jdbcTemplate.queryForObject(
                    """
                    SELECT COUNT(*)
                    FROM paiement
                    WHERE id_reservation = ?
                    """,
                    Long.class,
                    idReservation
            );

            assertEquals(0L, nombrePaiements);

        } finally {

            depart.countDown();
            executor.shutdownNow();
        }
    }


    @Test
    void shouldKeepConfirmedReservationWhenExpirationRunsConcurrently()
            throws Exception {

        // Création d'une réservation récente
        Reservation reservation = reservationService.createReservation(
                "expiration-a@test.fr",
                idSeance,
                1
        );

        Integer idReservation = reservation.getIdReservation();

        // Deux threads : paiement et expiration
        ExecutorService executor = Executors.newFixedThreadPool(2);

        CountDownLatch pret = new CountDownLatch(2);
        CountDownLatch depart = new CountDownLatch(1);

        try {

            // THREAD A : le client effectue son paiement
            Future<Paiement> paiementFutur = executor.submit(() -> {

                pret.countDown();
                depart.await();

                return paiementService.effectuerPaiement(
                        "expiration-a@test.fr",
                        idReservation
                );
            });

            // THREAD B : le système recherche les réservations expirées
            Future<?> expirationFuture = executor.submit(() -> {

                pret.countDown();
                depart.await();

                expirationService.expirerReservationsDeLaSeance(idSeance);

                return null;
            });

            // Attendre que les deux threads soient prêts
            assertTrue(pret.await(10, TimeUnit.SECONDS));

            // ACT : départ simultané
            depart.countDown();

            // Attendre les résultats des deux opérations
            Paiement paiement = paiementFutur.get(20, TimeUnit.SECONDS);
            expirationFuture.get(20, TimeUnit.SECONDS);

            // Le paiement doit être accepté
            assertEquals(
                    StatutPaiement.ACCEPTE,
                    paiement.getStatut()
            );

            // La réservation doit rester confirmée
            String statutReservation = jdbcTemplate.queryForObject(
                    """
                    SELECT statut
                    FROM reservation
                    WHERE id_reservation = ?
                    """,
                    String.class,
                    idReservation
            );

            assertEquals("CONFIRMEE", statutReservation);

            // La place doit toujours appartenir au client
            Integer idReservationPlace = jdbcTemplate.queryForObject(
                    """
                    SELECT id_reservation
                    FROM place_seance
                    WHERE id_seance = ?
                    """,
                    Integer.class,
                    idSeance
            );

            assertEquals(idReservation, idReservationPlace);

            // Un paiement ACCEPTE doit avoir été enregistré
            Long nombrePaiements = jdbcTemplate.queryForObject(
                    """
                    SELECT COUNT(*)
                    FROM paiement
                    WHERE id_reservation = ?
                      AND statut = 'ACCEPTE'
                    """,
                    Long.class,
                    idReservation
            );

            assertEquals(1L, nombrePaiements);

        } finally {

            // Déblocage éventuel des threads
            depart.countDown();

            // Arrêt du pool de threads
            executor.shutdownNow();
        }
    }


}
