package com.legrandecinema.le_grande_cinema.service.impl;

import com.legrandecinema.le_grande_cinema.repositories.PlaceSeanceRepository;
import com.legrandecinema.le_grande_cinema.service.PlaceSeanceService;
import org.springframework.stereotype.Service;

@Service
public class PlaceSeanceServiceImpl implements PlaceSeanceService {

    private final PlaceSeanceRepository placeSeanceRepository;

    public PlaceSeanceServiceImpl(PlaceSeanceRepository placeSeanceRepository) {
        this.placeSeanceRepository = placeSeanceRepository;
    }

    @Override
    public long getNombrePlacesDisponibles(Integer idSeance){
        return placeSeanceRepository.countBySeanceIdSeanceAndReservationIsNull(idSeance);
    }

}
