package com.legrandecinema.le_grande_cinema.controller;

import com.legrandecinema.le_grande_cinema.dto.ReservationDto;
import com.legrandecinema.le_grande_cinema.mapper.ReservationMapper;
import com.legrandecinema.le_grande_cinema.model.Reservation;
import com.legrandecinema.le_grande_cinema.service.ReservationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/seances")
@Tag(
        name = "Administration : Réservations",
        description = "Consultation des réservations d'une séance par un administrateur"
)
@SecurityRequirement(name = "basicAuth")
public class AdminReservationController {

    private final ReservationService reservationService;

    public AdminReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/{id}/reservations")
    @Operation(
            summary = "Récupérer les réservations d'une séance",
            description = "Retourne la liste des réservations associées à une séance."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste des réservations récupérée avec succès"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Utilisateur non authentifié"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Accès réservé aux administrateurs"
            )
    })
    public ResponseEntity<List<ReservationDto>> getReservationsBySeance(

            @Parameter(
                    description = "Identifiant de la séance",
                    example = "1"
            )
            @PathVariable Integer id
    ) {

        List<Reservation> reservations =
                reservationService.getReservationsBySeance(id);

        List<ReservationDto> reservationDtos = reservations
                .stream()
                .map(ReservationMapper::toDto)
                .toList();

        return ResponseEntity.ok(reservationDtos);
    }
}