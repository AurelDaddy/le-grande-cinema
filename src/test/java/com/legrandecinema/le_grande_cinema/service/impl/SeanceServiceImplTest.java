package com.legrandecinema.le_grande_cinema.service.impl;

import com.legrandecinema.le_grande_cinema.model.Seance;
import com.legrandecinema.le_grande_cinema.repositories.SeanceRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SeanceServiceImplTest {

    @Test
    void shouldReturnProchainesSeancesDunFilm(){
        SeanceRepository seanceRepository = Mockito.mock(SeanceRepository.class);

        SeanceServiceImpl seanceService = new SeanceServiceImpl(seanceRepository);

        Seance seance1 = new Seance();
        Seance seance2 = new Seance();

        List<Seance> prochaineSeance = List.of(seance1,seance2);

        Mockito.when(
                seanceRepository.findByFilmIdFilmAndDateHeureAfter(
                        Mockito.eq(1),
                        Mockito.any(LocalDateTime.class)
                )
        )
                .thenReturn(prochaineSeance);

        List<Seance> seancesDunFilm = seanceService.getSeancesByFilm(1);

        assertEquals(prochaineSeance, seancesDunFilm);

        Mockito.verify(seanceRepository)
                .findByFilmIdFilmAndDateHeureAfter(
                        Mockito.eq(1),
                        Mockito.any(LocalDateTime.class)
                );

    }
}
