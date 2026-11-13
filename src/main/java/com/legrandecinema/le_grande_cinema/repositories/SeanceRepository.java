package com.legrandecinema.le_grande_cinema.repositories;

import com.legrandecinema.le_grande_cinema.model.Seance;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SeanceRepository extends JpaRepository<Seance, Integer> {

    List<Seance> findByFilmIdFilmAndDateHeureAfter(Integer idFilm, LocalDateTime dateHeure);

    // Verrouille la séance pendant la transaction de réservation
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seance s WHERE s.idSeance = :idSeance")
    Optional<Seance> findByIdForUpdate(
            @Param("idSeance") Integer idSeance);
}
