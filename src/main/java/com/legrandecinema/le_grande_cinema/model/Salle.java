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
@Table(name = "salle")
public class Salle {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id_salle")
private Integer idSalle;

@Column(name = "numero_salle", nullable = false, unique = true)
private int numeroSalle;

@OneToMany(mappedBy = "salle")
private List<Place> places;

@OneToMany(mappedBy = "salle")
private List<Seance> seances;

}
