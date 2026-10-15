package com.legrandecinema.le_grande_cinema.model;

import com.legrandecinema.le_grande_cinema.model.type.StatutPaiement;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "paiement")
public class Paiement {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id_paiement")
private Integer idPaiement;

@Column(name = "montant", nullable = false, precision = 5, scale = 2)
private BigDecimal montant;

@Column(name = "date_paiement", nullable = false)
private LocalDateTime datePaiement;

@Enumerated(EnumType.STRING)
@Column(name = "statut", nullable = false, length = 20)
private StatutPaiement statut;

@Column(name = "reference_paiement", length = 20)
private String referencePaiement;

@ManyToOne
@JoinColumn(name = "id_reservation",  nullable = false)
private Reservation reservation;

}
