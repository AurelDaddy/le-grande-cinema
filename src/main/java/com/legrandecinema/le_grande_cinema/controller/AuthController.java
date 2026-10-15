package com.legrandecinema.le_grande_cinema.controller;

import com.legrandecinema.le_grande_cinema.dto.CreateRegisterRequest;
import com.legrandecinema.le_grande_cinema.dto.UtilisateurDto;
import com.legrandecinema.le_grande_cinema.mapper.UtilisateurMapper;
import com.legrandecinema.le_grande_cinema.model.Utilisateur;
import com.legrandecinema.le_grande_cinema.service.UtilisateurService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UtilisateurService utilisateurService;
    public AuthController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @PostMapping("/inscription")
    public ResponseEntity<UtilisateurDto> inscription(@RequestBody CreateRegisterRequest request) {

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setPrenom(request.prenom());
        utilisateur.setNom(request.nom());
        utilisateur.setTelephone(request.telephone());
        utilisateur.setEmail(request.email());
        utilisateur.setMotDePasse(request.motDePasse());

        Utilisateur utilisateurEnregistre = utilisateurService.inscrireUtilisateur(utilisateur);

        UtilisateurDto utilisateurDto = UtilisateurMapper.toDto(utilisateurEnregistre);

        return ResponseEntity.ok(utilisateurDto);
    }

    @GetMapping("/moi")
    public ResponseEntity<UtilisateurDto> moi(Authentication authentication) {

        String email = authentication.getName();

        Utilisateur utilisateur = utilisateurService
                .getUtilisateurByEmail(email)
                .orElseThrow();

        UtilisateurDto utilisateurDto = UtilisateurMapper.toDto(utilisateur);

        return ResponseEntity.ok(utilisateurDto);
    }
}



