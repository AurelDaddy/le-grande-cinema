package com.legrandecinema.le_grande_cinema.service.impl;

import com.legrandecinema.le_grande_cinema.model.Place;
import com.legrandecinema.le_grande_cinema.model.PlaceSeance;
import com.legrandecinema.le_grande_cinema.model.Salle;
import com.legrandecinema.le_grande_cinema.model.Seance;
import com.legrandecinema.le_grande_cinema.repositories.PlaceRepository;
import com.legrandecinema.le_grande_cinema.repositories.PlaceSeanceRepository;
import com.legrandecinema.le_grande_cinema.repositories.ReservationRepository;
import com.legrandecinema.le_grande_cinema.repositories.SeanceRepository;
import com.legrandecinema.le_grande_cinema.service.ReservationService;
import com.legrandecinema.le_grande_cinema.service.SeanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SeanceServiceImplTest {

    private SeanceRepository seanceRepository;
    private PlaceRepository placeRepository;
    private PlaceSeanceRepository placeSeanceRepository;
    private SeanceService seanceService;
    private ReservationRepository reservationRepository;

    @BeforeEach
    void setUp() {
        seanceRepository = Mockito.mock(SeanceRepository.class);
        placeRepository = Mockito.mock(PlaceRepository.class);
        placeSeanceRepository = Mockito.mock(PlaceSeanceRepository.class);
        reservationRepository = Mockito.mock(ReservationRepository.class);

        seanceService = new SeanceServiceImpl(
                seanceRepository,
                placeRepository,
                placeSeanceRepository,
                reservationRepository
        );
    }

    @Test
    void shouldReturnProchainesSeancesDunFilm(){

        Seance seance1 = new Seance();
        Seance seance2 = new Seance();

        List<Seance> prochainesSeance = List.of(seance1,seance2);

        Mockito.when(
                seanceRepository.findByFilmIdFilmAndDateHeureAfter(
                        Mockito.eq(1),
                        Mockito.any(LocalDateTime.class)
                )
        )
                .thenReturn(prochainesSeance);

        List<Seance> seancesDunFilm = seanceService.getSeancesByFilm(1);

        assertEquals(prochainesSeance, seancesDunFilm);

        Mockito.verify(seanceRepository)
                .findByFilmIdFilmAndDateHeureAfter(
                        Mockito.eq(1),
                        Mockito.any(LocalDateTime.class)
                );

    }

    @Test
    void shouldCreatePlaceSeancesForEachPlaceWhenCreationSeance(){
        Seance seance = new Seance();

        Salle salle = new Salle();
        salle.setIdSalle(1);
        seance.setSalle(salle);

        Place place1 = new Place();
        Place place2 = new Place();
        Place place3 = new Place();

        List<Place> places = List.of(place1,place2,place3);

        Mockito.when(seanceRepository.save(seance))
                .thenReturn(seance);

        Mockito.when(placeRepository.findBySalleIdSalle(1))
                .thenReturn(places);

        seanceService.createSeance(seance);

        //on déclare un ArgumentCaptor afin de capter la Liste de PlaceSeance
        ArgumentCaptor<List<PlaceSeance>> captor = ArgumentCaptor.forClass(List.class);

        //on vérifie que saveAll a bien été appelé par placeSeanceRepository et on capture l'argument qui lui été passé. Ici la List<PlaceSeance>
        Mockito.verify(placeSeanceRepository).saveAll(captor.capture());

        //on récupère la liste et on l'inspecte
        List<PlaceSeance> placesSeanceSauvegardees = captor.getValue();

        //vérfications métier
        assertEquals(3, placesSeanceSauvegardees.size());

        //vérification que chaque placeSeance est associée à la bonne place
        assertEquals(place1, placesSeanceSauvegardees.get(0).getPlace());
        assertEquals(place2, placesSeanceSauvegardees.get(1).getPlace());
        assertEquals(place3, placesSeanceSauvegardees.get(2).getPlace());

        //vérification que chaque placeSeance reçoit la bonne séance
        assertEquals(seance, placesSeanceSauvegardees.get(0).getSeance());
        assertEquals(seance, placesSeanceSauvegardees.get(1).getSeance());
        assertEquals(seance, placesSeanceSauvegardees.get(2).getSeance());

        //vérification que chaque réservation associée est bien null
        assertNull(placesSeanceSauvegardees.get(0).getReservation());
        assertNull(placesSeanceSauvegardees.get(1).getReservation());
        assertNull(placesSeanceSauvegardees.get(2).getReservation());

    }

    @Test
    void shouldDeleteSeanceWhenNoReservationExists(){
        Mockito.when(
                reservationRepository.existsBySeanceIdSeance(10))
                .thenReturn(Boolean.FALSE);

        seanceService.deleteSeance(10);

        Mockito.verify(seanceRepository).deleteById(10);
    }

    @Test
    void shouldNotDeleteSeanceWhenReservationExists(){

        Mockito.when(
                reservationRepository.existsBySeanceIdSeance(10))
                .thenReturn(Boolean.TRUE);

        assertThrows(
                IllegalStateException.class,
                () -> seanceService.deleteSeance(10)
        );

        Mockito.verify(reservationRepository, Mockito.never())
                .deleteById(10);
    }
}
