package com.clinicpro.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

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
public class ConsultationResponse {

    private UUID id;
    private RendezVousResponse rendezVous;
    private String diagnostic;
    private String ordonnance;
    private BigDecimal prix;
}
