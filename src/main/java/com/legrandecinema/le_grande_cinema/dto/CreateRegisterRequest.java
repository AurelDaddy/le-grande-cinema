package com.legrandecinema.le_grande_cinema.dto;

public record CreateRegisterRequest(
        String prenom,
        String nom,
        String email,
        String motDePasse,
        String telephone
) {
}
