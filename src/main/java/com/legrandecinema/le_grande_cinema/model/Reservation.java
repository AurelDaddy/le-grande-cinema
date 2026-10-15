package com.legrandecinema.le_grande_cinema.model;

import com.legrandecinema.le_grande_cinema.model.type.StatutReservation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "reservation")
public class Reservation {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id_reservation")
private Integer idReservation;

@Column(name = "date_reservation", nullable = false)
private LocalDateTime dateReservation;

@Enumerated(EnumType.STRING)
@Column(name = "statut", nullable = false,  length = 20)
private StatutReservation statut;

@ManyToOne
@JoinColumn(name = "id_utilisateur", nullable = false)
private Utilisateur utilisateur;

@ManyToOne
@JoinColumn(name = "id_seance", nullable = false)
private Seance seance;

@OneToMany(mappedBy = "reservation")
private List<PlaceSeance> placeSeances;

@OneToMany(mappedBy = "reservation")
private List<Paiement>  paiements;
}
