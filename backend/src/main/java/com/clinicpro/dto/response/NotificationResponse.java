package com.clinicpro.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.clinicpro.entity.StatutNotification;
import com.clinicpro.entity.TypeNotification;

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
public class NotificationResponse {

    private UUID id;
    private RendezVousResponse rendezVous;
    private TypeNotification type;
    private String message;
    private LocalDateTime dateEnvoi;
    private StatutNotification statut;
}
