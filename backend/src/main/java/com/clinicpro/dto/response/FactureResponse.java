package com.clinicpro.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.clinicpro.entity.StatutPaiement;

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
public class FactureResponse {

    private UUID id;
    private String numeroFacture;
    private ConsultationResponse consultation;
    private BigDecimal montant;
    private LocalDateTime dateEmission;
    private StatutPaiement statutPaiement;
}
