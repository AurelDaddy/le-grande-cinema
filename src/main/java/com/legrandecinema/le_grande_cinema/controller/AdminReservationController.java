package com.legrandecinema.le_grande_cinema.controller;


import com.legrandecinema.le_grande_cinema.dto.ReservationDto;
import com.legrandecinema.le_grande_cinema.mapper.ReservationMapper;
import com.legrandecinema.le_grande_cinema.model.Reservation;
import com.legrandecinema.le_grande_cinema.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/seances")
public class AdminReservationController {

    private final ReservationService reservationService;

    public AdminReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/{id}/reservations")
    public ResponseEntity<List<ReservationDto>> getReservationsBySeance (@PathVariable Integer id) {

        List<Reservation> reservations = reservationService.getReservationsBySeance(id);

        List<ReservationDto> reservationDtos = reservations.stream()
                .map(ReservationMapper::toDto)
                .toList();

        return ResponseEntity.ok(reservationDtos);
    }
}
