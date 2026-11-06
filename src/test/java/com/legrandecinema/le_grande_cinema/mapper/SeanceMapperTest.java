
package com.legrandecinema.le_grande_cinema.mapper;

import com.legrandecinema.le_grande_cinema.dto.SeanceDto;
import com.legrandecinema.le_grande_cinema.model.Film;
import com.legrandecinema.le_grande_cinema.model.Salle;
import com.legrandecinema.le_grande_cinema.model.Seance;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SeanceMapperTest {

    @Test
    void shouldRespecterLesIdentifiantsDuFilmEtDeLaSalle() {

        // Arrange : préparer les données du test
        Film film = new Film();
        film.setIdFilm(7);

        Salle salle = new Salle();
        salle.setIdSalle(3);

        Seance seance = new Seance();
        seance.setIdSeance(12);
        seance.setFilm(film);
        seance.setSalle(salle);

        // Act : exécuter la méthode à tester
        SeanceDto resultat = SeanceMapper.toDto(seance);

        // Assert : vérifier le résultat
        assertEquals(12, resultat.idSeance());
        assertEquals(7, resultat.idFilm());
        assertEquals(3, resultat.idSalle());
    }
}
