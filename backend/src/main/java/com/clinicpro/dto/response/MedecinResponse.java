package com.clinicpro.dto.response;

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
public class MedecinResponse {

    private UUID id;
    private String nom;
    private String specialite;
    private String email;
    private String tel;
}