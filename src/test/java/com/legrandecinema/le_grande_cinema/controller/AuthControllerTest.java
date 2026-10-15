package com.legrandecinema.le_grande_cinema.controller;

import com.legrandecinema.le_grande_cinema.dto.CreateRegisterRequest;
import com.legrandecinema.le_grande_cinema.model.Utilisateur;
import com.legrandecinema.le_grande_cinema.model.type.TypeUtilisateur;
import com.legrandecinema.le_grande_cinema.service.UtilisateurService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerTest {

    @Test
    void shouldInscrireUtilisateur() throws Exception {

        UtilisateurService utilisateurService = Mockito.mock(UtilisateurService.class);

        AuthController authController = new AuthController(utilisateurService);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        ObjectMapper objectMapper = new ObjectMapper();

        CreateRegisterRequest request = new CreateRegisterRequest(
                "michel",
                "Dumont",
                "michel.dumont@test.fr",
                "motdepasse",
                "06 12 13 14 15"
        );

        Mockito.when(utilisateurService.inscrireUtilisateur(Mockito.any(Utilisateur.class))
        ).thenAnswer(invocation -> {
            Utilisateur utilisateur = invocation.getArgument(0);
            utilisateur.setIdUtilisateur(9);
            utilisateur.setTypeUtilisateur(TypeUtilisateur.CLIENT);
            return utilisateur;
        });

        mockMvc.perform(
                        post("/api/auth/inscription")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUtilisateur").value(9))
                .andExpect(jsonPath("$.prenom").value("michel"))
                .andExpect(jsonPath("$.nom").value("Dumont"))
                .andExpect(jsonPath("$.email").value("michel.dumont@test.fr"))
                .andExpect(jsonPath("$.telephone").value("06 12 13 14 15"))
                .andExpect(jsonPath("$.typeUtilisateur").value("CLIENT"));

        ArgumentCaptor<Utilisateur> captor =
                ArgumentCaptor.forClass(Utilisateur.class);

        Mockito.verify(utilisateurService)
                .inscrireUtilisateur(captor.capture());

        Utilisateur utilisateurCree = captor.getValue();

        assertEquals("michel", utilisateurCree.getPrenom());
        assertEquals("Dumont", utilisateurCree.getNom());
        assertEquals("michel.dumont@test.fr", utilisateurCree.getEmail());
        assertEquals("motdepasse", utilisateurCree.getMotDePasse());
        assertEquals("06 12 13 14 15", utilisateurCree.getTelephone());


    }

    @Test
    void shouldReturnUtilisateurConnecte() throws Exception {

        UtilisateurService utilisateurService = Mockito.mock(UtilisateurService.class);
        AuthController authController = new AuthController(utilisateurService);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setIdUtilisateur(9);
        utilisateur.setPrenom("michel");
        utilisateur.setNom("Dumont");
        utilisateur.setEmail("michel.dumont@test.fr");
        utilisateur.setTelephone("06 12 13 14 15");
        utilisateur.setTypeUtilisateur(TypeUtilisateur.CLIENT);

        Mockito.when(
                utilisateurService.getUtilisateurByEmail(
                        "michel.dumont@test.fr"
                )
        ).thenReturn(Optional.of(utilisateur));


        //on simule un utilisateur authentifié. UsernamePasswordAuthentficationToken
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "michel.dumont@test.fr",
                        null //ici credentials (mot de passe) null car on ne teste pas la vraie authentification HTTP Basic, on fournit simplement un objet authentication valide au controller
                );

        mockMvc.perform(
                        get("/api/auth/moi")
                                .principal(authentication) //attache principal donc le mail à la requête
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUtilisateur").value(9))
                .andExpect(jsonPath("$.prenom").value("michel"))
                .andExpect(jsonPath("$.nom").value("Dumont"))
                .andExpect(jsonPath("$.email").value("michel.dumont@test.fr"))
                .andExpect(jsonPath("$.telephone").value("06 12 13 14 15"))
                .andExpect(jsonPath("$.typeUtilisateur").value("CLIENT"));

        Mockito.verify(utilisateurService)
                .getUtilisateurByEmail("michel.dumont@test.fr");
    }


}

