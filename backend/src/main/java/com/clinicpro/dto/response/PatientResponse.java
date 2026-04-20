package com.clinicpro.dto.response;

import java.time.LocalDate;
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
public class PatientResponse {

    private UUID id;
    private String nom;
    private LocalDate dateNaissance;
    private String dossierMedical;
    private String tel;
}
