package com.clinicpro.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
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
public class ConsultationRequest {

    @NotNull(message = "L'identifiant du rendez-vous est obligatoire")
    private UUID rendezVousId;

    private String diagnostic;

    private String ordonnance;

    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.0", inclusive = true, message = "Le prix doit être positif ou nul")
    @Digits(integer = 8, fraction = 2, message = "Format de prix invalide")
    private BigDecimal prix;
}
