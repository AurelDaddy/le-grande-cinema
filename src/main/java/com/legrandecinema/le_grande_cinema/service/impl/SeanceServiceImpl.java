package com.legrandecinema.le_grande_cinema.service.impl;

import com.legrandecinema.le_grande_cinema.model.Place;
import com.legrandecinema.le_grande_cinema.model.PlaceSeance;
import com.legrandecinema.le_grande_cinema.model.Seance;
import com.legrandecinema.le_grande_cinema.repositories.PlaceRepository;
import com.legrandecinema.le_grande_cinema.repositories.PlaceSeanceRepository;
import com.legrandecinema.le_grande_cinema.repositories.ReservationRepository;
import com.legrandecinema.le_grande_cinema.repositories.SeanceRepository;
import com.legrandecinema.le_grande_cinema.service.SeanceService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SeanceServiceImpl implements SeanceService {

    // Référence vers l'instance de SeanceRepository injectée par Spring
    private final SeanceRepository seanceRepository;
    private final PlaceRepository placeRepository;
    private final PlaceSeanceRepository placeSeanceRepository;
    private final ReservationRepository reservationRepository;

    //Injection de SeanceRepository par le constructeur
    public SeanceServiceImpl(
            SeanceRepository seanceRepository,
            PlaceRepository placeRepository,
            PlaceSeanceRepository placeSeanceRepository, ReservationRepository reservationRepository) {
        this.seanceRepository = seanceRepository;
        this.placeRepository = placeRepository;
        this.placeSeanceRepository = placeSeanceRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public List<Seance> getSeancesByFilm(Integer idFilm) {
        return seanceRepository.findByFilmIdFilmAndDateHeureAfter(idFilm, LocalDateTime.now());
    }
    @Override
    public Optional<Seance> getSeanceById(Integer id) {
        return seanceRepository.findById(id);
    }

    @Transactional
    @Override
    public Seance createSeance(Seance seance) {

        //on enregistre d'abord la séance
        Seance seanceEnregistree = seanceRepository.save(seance);

        //on récupère l'idSalle de la séance
        Integer idSalle = seanceEnregistree.getSalle().getIdSalle();

        //on récupère toutes les places physiques de cette salle
        List<Place> placesDUneSalle = placeRepository.findBySalleIdSalle(idSalle);

        //pour chaque place physique on crée une placeSeance
        List<PlaceSeance> placesSeance = placesDUneSalle.stream()
                .map(place -> {
                    PlaceSeance placeSeance = new PlaceSeance();
                    placeSeance.setPlace(place);
                    placeSeance.setSeance(seanceEnregistree);
                    placeSeance.setReservation(null);

                    return placeSeance;
                })
                .toList();

        //on enregistre toutes les placeSeance
        placeSeanceRepository.saveAll(placesSeance);

        return seanceEnregistree;
    }

    @Override
    public void deleteSeance(Integer id) {

        //on vérifie qu'il n'y a pas de réservations associées à la séance
        boolean possedeReservation =
                reservationRepository.existsBySeanceIdSeance(id);

        if (possedeReservation){
            throw new IllegalStateException(
                    "Impossible de supprimer une séance avec des réservations"
            );
        }
        placeSeanceRepository.deleteBySeanceIdSeance(id);

        seanceRepository.deleteById(id);


    }
}
