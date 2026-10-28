package com.legrandecinema.le_grande_cinema.controller;

import com.legrandecinema.le_grande_cinema.dto.CreateRegisterRequest;
import com.legrandecinema.le_grande_cinema.dto.UtilisateurDto;
import com.legrandecinema.le_grande_cinema.exception.RessourceIntrouvableException;
import com.legrandecinema.le_grande_cinema.mapper.UtilisateurMapper;
import com.legrandecinema.le_grande_cinema.model.Utilisateur;
import com.legrandecinema.le_grande_cinema.service.UtilisateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(
        name = "Authentification",
        description = "Inscription et consultation de l'utilisateur connecté"
)
public class AuthController {

    private final UtilisateurService utilisateurService;

    public AuthController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @PostMapping("/inscription")
    @Operation(
            summary = "Inscrire un utilisateur",
            description = "Crée un nouveau compte utilisateur avec le rôle CLIENT."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Utilisateur inscrit avec succès"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Un utilisateur avec cet email existe déjà"
            )
    })
    public ResponseEntity<UtilisateurDto> inscription(

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Informations nécessaires à la création du compte",
                    required = true
            )
            @RequestBody CreateRegisterRequest request
    ) {

        Utilisateur utilisateur = new Utilisateur();

        utilisateur.setPrenom(request.prenom());
        utilisateur.setNom(request.nom());
        utilisateur.setTelephone(request.telephone());
        utilisateur.setEmail(request.email());
        utilisateur.setMotDePasse(request.motDePasse());

        Utilisateur utilisateurEnregistre =
                utilisateurService.inscrireUtilisateur(utilisateur);

        UtilisateurDto utilisateurDto =
                UtilisateurMapper.toDto(utilisateurEnregistre);

        return ResponseEntity.ok(utilisateurDto);
    }

    @GetMapping("/moi")
    @Operation(
            summary = "Récupérer l'utilisateur connecté",
            description = "Retourne les informations de l'utilisateur actuellement authentifié."
    )
    @SecurityRequirement(name = "basicAuth")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Utilisateur récupéré avec succès"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Utilisateur non authentifié"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Utilisateur introuvable"
            )
    })
    public ResponseEntity<UtilisateurDto> moi(
            Authentication authentication
    ) {

        String email = authentication.getName();

        Utilisateur utilisateur = utilisateurService
                .getUtilisateurByEmail(email)
                .orElseThrow(() ->
                        new RessourceIntrouvableException(
                                "Utilisateur introuvable"
                        )
                );

        UtilisateurDto utilisateurDto =
                UtilisateurMapper.toDto(utilisateur);

        return ResponseEntity.ok(utilisateurDto);
    }
}