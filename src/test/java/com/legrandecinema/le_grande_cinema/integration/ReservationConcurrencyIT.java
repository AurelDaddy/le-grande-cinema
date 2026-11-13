
package com.legrandecinema.le_grande_cinema.integration;

import com.legrandecinema.le_grande_cinema.model.*;
import com.legrandecinema.le_grande_cinema.model.type.TypeUtilisateur;
import com.legrandecinema.le_grande_cinema.service.ReservationService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("integration-mysql")
public class ReservationConcurrencyIT {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Integer idSeance;

    @BeforeEach
    void prepareDonnees() {

        // ARRANGE : préparation des données dans une transaction
        idSeance = transactionTemplate.execute(status -> {

            // Création de deux utilisateurs
            creerUtilisateur("jean-michel@test.fr");
            creerUtilisateur("jean-claude@test.fr");

            // Création du film
            Film film = new Film();
            film.setTitre("2 Flics à Miami");
            film.setDureeMinute(90);
            film.setAnneeSortie(1986);
            film.setGenre("Action");
            film.setRealisateur("Michael Mann");
            film.setSynopsis("c'est bien..................");
            film.setUrlAffiche("https://example.com/film.jpg");
            entityManager.persist(film);

            // Création d'une salle
            Salle salle = new Salle();
            salle.setNumeroSalle(1);
            entityManager.persist(salle);

            // Création d'une séance future
            Seance seance = new Seance();
            seance.setFilm(film);
            seance.setSalle(salle);
            seance.setDateHeure(LocalDateTime.now().plusDays(7));
            seance.setVersion("VF");
            seance.setPrix(new BigDecimal("12.50"));
            entityManager.persist(seance);

            // Création d'une seule place physique
            Place place = new Place();
            place.setSalle(salle);
            place.setRang("A");
            place.setNumero(1);
            entityManager.persist(place);

            // Cette place est disponible pour notre séance
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
        utilisateur.setPrenom("Prénom");
        utilisateur.setNom("Nom");
        utilisateur.setEmail(email);
        utilisateur.setMotDePasse("mot-de-passe");
        utilisateur.setTypeUtilisateur(TypeUtilisateur.CLIENT);

        entityManager.persist(utilisateur);
    }

    @Test
    void shouldPreventDoubleBookingOfLastSeat() throws Exception {

        // ARRANGE : deux threads représentent deux clients
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Les deux threads signaleront qu'ils sont prêts
        CountDownLatch pret = new CountDownLatch(2);

        // Ce signal permettra de les lancer ensemble
        CountDownLatch depart = new CountDownLatch(1);

        try {

            Future<Boolean> resultatA = executor.submit(() -> {
                pret.countDown();
                depart.await();

                try {
                    reservationService.createReservation(
                            "jean-michel@test.fr",
                            idSeance,
                            1
                    );
                    return true;
                } catch (IllegalStateException exception) {
                    return false;
                }
            });

            Future<Boolean> resultatB = executor.submit(() -> {
                pret.countDown();
                depart.await();

                try {
                    reservationService.createReservation(
                            "jean-claude@test.fr",
                            idSeance,
                            1
                    );
                    return true;
                } catch (IllegalStateException exception) {
                    return false;
                }
            });

            // On attend que les deux clients soient prêts
            assertTrue(pret.await(10, TimeUnit.SECONDS));

            // ACT : départ simultané des deux réservations
            depart.countDown();

            boolean reservationAReussie =
                    resultatA.get(20, TimeUnit.SECONDS);

            boolean reservationBReussie =
                    resultatB.get(20, TimeUnit.SECONDS);

            int nombreReservationsReussies =
                    (reservationAReussie ? 1 : 0)
                            + (reservationBReussie ? 1 : 0);

            // ASSERT : une seule réservation doit réussir
            assertEquals(1, nombreReservationsReussies,
                    "Une seule réservation doit réussir");

            // Vérification des données réellement enregistrées
            Long nombreReservationsEnBase =
                    jdbcTemplate.queryForObject(
                            "SELECT COUNT(*) FROM reservation WHERE id_seance = ?",
                            Long.class,
                            idSeance
                    );

            assertEquals(1L, nombreReservationsEnBase,
                    "Une seule réservation doit exister en base");

            // La dernière place ne doit plus être disponible
            Long placesRestantes =
                    jdbcTemplate.queryForObject(
                            """
                            SELECT COUNT(*)
                            FROM place_seance
                            WHERE id_seance = ?
                              AND id_reservation IS NULL
                            """,
                            Long.class,
                            idSeance
                    );

            assertEquals(0L, placesRestantes,
                    "La dernière place doit être réservée");

        } finally {
            // Libération des threads même si le test échoue
            depart.countDown();
            executor.shutdownNow();
        }
    }
}
