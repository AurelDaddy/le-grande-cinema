package com.legrandecinema.le_grande_cinema.repositories;

import com.legrandecinema.le_grande_cinema.model.Reservation;
import com.legrandecinema.le_grande_cinema.model.type.StatutReservation;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    List<Reservation> findByStatut(StatutReservation statut);

    Boolean existsBySeanceIdSeance(Integer idSeance); //existsBy car pas besoin de charger toutes les réservations en mémoire, on veut juste une réponse true or false

    List<Reservation> findBySeanceIdSeance(Integer idSeance);

    List<Reservation> findByUtilisateurEmailAndStatut(String email, StatutReservation statut);

    List<Reservation> findByStatutAndDateReservationBefore(StatutReservation statut, LocalDateTime dateLimite);


    // Récupère uniquement l'identifiant de la séance liée à une réservation, sans charger toute l'entité.
    @Query("""
           SELECT r.seance.idSeance
           FROM Reservation r
           WHERE r.idReservation = :idReservation
           """)
    Optional<Integer> findSeanceIdByReservationId(
            @Param("idReservation") Integer idReservation
    );


    // Lit et verrouille la réservation afin d'éviter toute modification concurrente pendant le paiement.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT r
            FROM Reservation r
            WHERE r.idReservation = :idReservation
            """)
    Optional<Reservation> findByIdForUpdate(
            @Param("idReservation") Integer idReservation
    );


}
