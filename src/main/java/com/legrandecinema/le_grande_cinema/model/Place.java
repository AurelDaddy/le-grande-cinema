package com.legrandecinema.le_grande_cinema.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "place")
public class Place {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id_place")
private Integer idPlace;

@Column(name = "rang", nullable = false, length = 2)
private String rang;

@Column(name = "numero", nullable = false)
private int numero;

@ManyToOne
@JoinColumn(name = "id_salle", nullable = false)
private Salle salle;

@OneToMany(mappedBy = "place")
private List<PlaceSeance> placeSeances;
}
