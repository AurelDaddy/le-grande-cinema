package com.legrandecinema.le_grande_cinema.service;

import com.legrandecinema.le_grande_cinema.dto.BilletDto;

import java.util.List;

public interface BilletService {

    List<BilletDto> getBilletsUtilisateur(String email);

    BilletDto getBilletByReservation(String email, Integer idReservation);
}
