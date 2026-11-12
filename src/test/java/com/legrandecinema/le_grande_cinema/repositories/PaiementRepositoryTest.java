
package com.legrandecinema.le_grande_cinema.repositories;

import com.legrandecinema.le_grande_cinema.model.*;
import com.legrandecinema.le_grande_cinema.model.type.StatutPaiement;
import com.legrandecinema.le_grande_cinema.model.type.StatutReservation;
import com.legrandecinema.le_grande_cinema.model.type.TypeUtilisateur;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class PaiementRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldSavePaymentOver999Point99Euros() {

        // ARRANGE : création des données nécessaires au paiement

        // Création de l'utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setPrenom("Jean");
        utilisateur.setNom("Test");
        utilisateur.setEmail("jeantest@cinema.fr");
        utilisateur.setMotDePasse("mot-de-passe");
        utilisateur.setTypeUtilisateur(TypeUtilisateur.CLIENT);

        entityManager.persist(utilisateur);

        // Création du film
        Film film = new Film();
        film.setTitre("Titanic");
        film.setDureeMinute(180);
        film.setAnneeSortie(1997);
        film.setGenre("Romance");
        film.setRealisateur("James Cameron");
        film.setSynopsis("C'est l'histoire d'un bateau qui coule");
        film.setUrlAffiche("https://example.com/affiche.jpg");

        entityManager.persist(film);

        // Création de la salle
        Salle salle = new Salle();
        salle.setNumeroSalle(99);

        entityManager.persist(salle);

        // Création de la séance
        Seance seance = new Seance();
        seance.setDateHeure(LocalDateTime.now().plusDays(10));
        seance.setVersion("VF");
        seance.setPrix(new BigDecimal("12.50"));
        seance.setFilm(film);
        seance.setSalle(salle);

        entityManager.persist(seance);

        // Création d'une réservation
        Reservation reservation = new Reservation();
        reservation.setDateReservation(LocalDateTime.now());
        reservation.setStatut(StatutReservation.EN_ATTENTE);
        reservation.setUtilisateur(utilisateur);
        reservation.setSeance(seance);

        entityManager.persist(reservation);

        // Création d'un paiement supérieur à 999,99 €
        Paiement paiement = new Paiement();
        paiement.setMontant(new BigDecimal("1250.00"));
        paiement.setDatePaiement(LocalDateTime.now());
        paiement.setStatut(StatutPaiement.ACCEPTE);
        paiement.setReservation(reservation);

        // ACT : enregistrement réel dans la base H2
        entityManager.persist(paiement);
        entityManager.flush();

        Integer idPaiement = paiement.getIdPaiement();

        // On vide le contexte JPA pour forcer une lecture en base
        entityManager.clear();

        Paiement paiementEnregistre =
                entityManager.find(Paiement.class, idPaiement);

        // ASSERT : vérification du paiement enregistré
        assertNotNull(paiementEnregistre);

        assertEquals(
                new BigDecimal("1250.00"),
                paiementEnregistre.getMontant()
        );

        assertEquals(
                StatutPaiement.ACCEPTE,
                paiementEnregistre.getStatut()
        );
    }
}
