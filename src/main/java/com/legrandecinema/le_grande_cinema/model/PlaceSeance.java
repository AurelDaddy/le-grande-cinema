package com.legrandecinema.le_grande_cinema.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "place_seance")
public class PlaceSeance {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id_place_seance")
private Integer idPlaceSeance;

@ManyToOne
@JoinColumn(name = "id_reservation")
private Reservation reservation;

@ManyToOne
@JoinColumn(name = "id_seance", nullable = false)
private Seance seance;

@ManyToOne
@JoinColumn(name = "id_place", nullable = false)
private Place place;

}
