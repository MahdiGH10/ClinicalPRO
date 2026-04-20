package com.clinicpro.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.clinicpro.entity.StatutRendezVous;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RendezVousResponse {

    private UUID id;
    private UUID patientId;
    private String patientNom;
    private UUID medecinId;
    private String medecinNom;
    private LocalDateTime dateHeure;
    private String motif;
    private StatutRendezVous statut;
}
