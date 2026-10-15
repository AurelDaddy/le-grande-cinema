package com.legrandecinema.le_grande_cinema.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "seance")
public class Seance {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id_seance")
private Integer idSeance;

@Column(name = "date_heure", nullable = false)
private LocalDateTime dateHeure;

@Column(name = "version", nullable = false, length = 20)
private String version;

@Column(name = "prix", nullable = false, precision = 5, scale = 2)
private BigDecimal prix;

@ManyToOne
@JoinColumn(name = "id_salle", nullable = false)
private Salle salle;

@ManyToOne
@JoinColumn(name = "id_film", nullable = false)
private Film film;

@OneToMany(mappedBy = "seance")
private List<Reservation> reservations;

@OneToMany(mappedBy = "seance")
private List<PlaceSeance> placeSeances;

}
