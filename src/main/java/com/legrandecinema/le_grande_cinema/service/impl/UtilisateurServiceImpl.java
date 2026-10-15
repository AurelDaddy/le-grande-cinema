package com.legrandecinema.le_grande_cinema.service.impl;

import com.legrandecinema.le_grande_cinema.model.Utilisateur;
import com.legrandecinema.le_grande_cinema.model.type.TypeUtilisateur;
import com.legrandecinema.le_grande_cinema.repositories.UtilisateurRepository;
import com.legrandecinema.le_grande_cinema.service.UtilisateurService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UtilisateurServiceImpl implements UtilisateurService {

       private final UtilisateurRepository utilisateurRepository;
       private final PasswordEncoder passwordEncoder;

        public UtilisateurServiceImpl(UtilisateurRepository utilisateurRepository,
                                      PasswordEncoder passwordEncoder
        ) {
            this.utilisateurRepository = utilisateurRepository;
            this.passwordEncoder = passwordEncoder;
        }

        @Override
        public Utilisateur inscrireUtilisateur(Utilisateur utilisateur) {

            if(utilisateurRepository.findByEmail(utilisateur.getEmail()).isPresent()){
                throw new IllegalStateException(
                        "Un utilisateur avec cet email existe déjà"
                );
            }

            utilisateur.setMotDePasse(
                    passwordEncoder.encode(utilisateur.getMotDePasse())
            );

            utilisateur.setTypeUtilisateur(TypeUtilisateur.CLIENT);

            return utilisateurRepository.save(utilisateur);
        }

        @Override
        public Optional<Utilisateur> getUtilisateurByEmail(String email) {
            return utilisateurRepository.findByEmail(email);
        }
}
