package com.legrandecinema.le_grande_cinema.service.impl;

import com.legrandecinema.le_grande_cinema.model.Utilisateur;
import com.legrandecinema.le_grande_cinema.model.type.TypeUtilisateur;
import com.legrandecinema.le_grande_cinema.repositories.UtilisateurRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UtilisateurServiceImplTest {

    @Test
    void shouldRegisterUtilisateur() {

        UtilisateurRepository utilisateurRepository =  Mockito.mock(UtilisateurRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);

        UtilisateurServiceImpl utilisateurService = new UtilisateurServiceImpl(
                utilisateurRepository,
                passwordEncoder
        );

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail("test@test.fr");
        utilisateur.setMotDePasse("motDePasse");

        Mockito.when(utilisateurRepository.findByEmail("test@test.fr"))
                .thenReturn(Optional.empty());

        Mockito.when(passwordEncoder.encode("motDePasse")).thenReturn("motDePasseEncode");

        Mockito.when(utilisateurRepository.save(utilisateur)).thenReturn(utilisateur);

        Utilisateur utilisateurEnregistre = utilisateurService.inscrireUtilisateur(utilisateur);

        assertEquals(
                "motDePasseEncode",
                utilisateurEnregistre.getMotDePasse()
        );

        assertEquals(
                TypeUtilisateur.CLIENT,
                utilisateurEnregistre.getTypeUtilisateur()
        );

        Mockito.verify(passwordEncoder).encode("motDePasse");

        Mockito.verify(utilisateurRepository).save(utilisateur);
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {

        UtilisateurRepository utilisateurRepository =
                Mockito.mock(UtilisateurRepository.class);

        PasswordEncoder passwordEncoder =
                Mockito.mock(PasswordEncoder.class);

        UtilisateurServiceImpl utilisateurService =
                new UtilisateurServiceImpl(
                        utilisateurRepository,
                        passwordEncoder
                );

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail("test@test.fr");
        utilisateur.setMotDePasse("motDePasse");

        Mockito.when(
                utilisateurRepository.findByEmail("test@test.fr")
        ).thenReturn(Optional.of(utilisateur));

        assertThrows(
                IllegalStateException.class,
                () -> utilisateurService.inscrireUtilisateur(utilisateur)
        );

        Mockito.verify(passwordEncoder, Mockito.never())
                .encode(Mockito.anyString());

        Mockito.verify(utilisateurRepository, Mockito.never())
                .save(Mockito.any(Utilisateur.class));
    }
}
