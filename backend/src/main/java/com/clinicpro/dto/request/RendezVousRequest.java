package com.clinicpro.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

import com.clinicpro.entity.StatutRendezVous;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class RendezVousRequest {

    @NotNull(message = "Le patient est obligatoire")
    private UUID patientId;

    @NotNull(message = "Le médecin est obligatoire")
    private UUID medecinId;

    @NotNull(message = "La date et l'heure sont obligatoires")
    @Future(message = "La date et l'heure du rendez-vous doivent être dans le futur")
    private LocalDateTime dateHeure;

    @NotBlank(message = "Le motif est obligatoire")
    @Size(max = 500, message = "Le motif ne doit pas dépasser 500 caractères")
    private String motif;

    @NotNull(message = "Le statut est obligatoire")
    private StatutRendezVous statut;
}
