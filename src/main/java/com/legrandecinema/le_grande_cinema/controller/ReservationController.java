package com.legrandecinema.le_grande_cinema.controller;

import com.legrandecinema.le_grande_cinema.dto.CreateReservationRequest;
import com.legrandecinema.le_grande_cinema.dto.ReservationDto;
import com.legrandecinema.le_grande_cinema.mapper.ReservationMapper;
import com.legrandecinema.le_grande_cinema.model.Reservation;
import com.legrandecinema.le_grande_cinema.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationDto> createReservation(
            @RequestBody CreateReservationRequest request,
            Authentication authentication){

        String email = authentication.getName();

        Reservation reservation = reservationService.createReservation(
                email,
                request.idSeance(),
                request.nombrePlaces());

        ReservationDto reservationDto = ReservationMapper.toDto(reservation);

        return ResponseEntity.ok(reservationDto);
    }


}
