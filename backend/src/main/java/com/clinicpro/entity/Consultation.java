package com.clinicpro.entity;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "consultations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rendez_vous_id", nullable = false, unique = true)
    private RendezVous rendezVous;

    @Column(columnDefinition = "TEXT")
    private String diagnostic;

    @Column(columnDefinition = "TEXT")
    private String ordonnance;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prix;
}
