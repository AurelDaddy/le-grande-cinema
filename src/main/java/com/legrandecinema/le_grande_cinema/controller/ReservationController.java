package com.legrandecinema.le_grande_cinema.controller;

import com.legrandecinema.le_grande_cinema.dto.CreateReservationRequest;
import com.legrandecinema.le_grande_cinema.dto.ReservationDto;
import com.legrandecinema.le_grande_cinema.mapper.ReservationMapper;
import com.legrandecinema.le_grande_cinema.model.Reservation;
import com.legrandecinema.le_grande_cinema.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/reservations")
@Tag(
        name  = "Réservations",
        description = "Création des réservations d'un utilisateur authentifié"
)
@SecurityRequirement(name = "basicAuth")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    @Operation(
            summary = "Création d'une réservation",
            description = "crée une réservation pour un utilisateur authentifié à partir du nombre de places demandées et de la séance"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Réservation créée avec succès"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Demande de réservation invalide"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Utilisateur non authentifié"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Réservation impossible, par exemple en cas de places insuffisantes"
            )
    })

    public ResponseEntity<ReservationDto> createReservation(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Informations nécessaires à la création de la réservation",
                    required = true
            )
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
