package com.clinicpro.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clinicpro.dto.response.NotificationResponse;
import com.clinicpro.dto.response.RendezVousResponse;
import com.clinicpro.entity.Notification;
import com.clinicpro.entity.RendezVous;
import com.clinicpro.entity.StatutNotification;
import com.clinicpro.entity.StatutRendezVous;
import com.clinicpro.entity.TypeNotification;
import com.clinicpro.exception.ResourceNotFoundException;
import com.clinicpro.repository.NotificationRepository;
import com.clinicpro.repository.RendezVousRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final RendezVousRepository rendezVousRepository;

    @Transactional(readOnly = true)
    public List<NotificationResponse> findAll() {
        return notificationRepository.findAll().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public NotificationResponse findById(UUID id) {
        Notification notification = notificationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Notification", id.toString()));
        return toResponse(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> findByRendezVousId(UUID rendezVousId) {
        return notificationRepository.findByRendezVous_Id(rendezVousId).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> findByStatut(StatutNotification statut) {
        return notificationRepository.findByStatut(statut).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public NotificationResponse sendConfirmation(UUID rendezVousId) {
        RendezVous rendezVous = getRendezVous(rendezVousId);
        String message = String.format(
            "Votre rendez-vous du %s avec Dr %s est confirmé.",
            rendezVous.getDateHeure(),
            rendezVous.getMedecin().getNom());

        return toResponse(createAndMarkSent(rendezVous, TypeNotification.CONFIRMATION, message));
    }

    public NotificationResponse sendCancellation(UUID rendezVousId) {
        RendezVous rendezVous = getRendezVous(rendezVousId);
        String message = String.format(
            "Votre rendez-vous du %s avec Dr %s est annulé.",
            rendezVous.getDateHeure(),
            rendezVous.getMedecin().getNom());

        return toResponse(createAndMarkSent(rendezVous, TypeNotification.ANNULATION, message));
    }

    public NotificationResponse sendReminder(UUID rendezVousId) {
        RendezVous rendezVous = getRendezVous(rendezVousId);
        String message = String.format(
            "Rappel: vous avez un rendez-vous le %s avec Dr %s.",
            rendezVous.getDateHeure(),
            rendezVous.getMedecin().getNom());

        return toResponse(createAndMarkSent(rendezVous, TypeNotification.RAPPEL, message));
    }

    public List<NotificationResponse> sendDailyReminders(LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now().plusDays(1);
        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = targetDate.plusDays(1).atStartOfDay();

        return rendezVousRepository.findByStatutAndDateHeureBetween(StatutRendezVous.CONFIRME, start, end).stream()
            .map(rendezVous -> {
                String message = String.format(
                    "Rappel: vous avez un rendez-vous le %s avec Dr %s.",
                    rendezVous.getDateHeure(),
                    rendezVous.getMedecin().getNom());
                return createAndMarkSent(rendezVous, TypeNotification.RAPPEL, message);
            })
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    private RendezVous getRendezVous(UUID rendezVousId) {
        return rendezVousRepository.findById(rendezVousId)
            .orElseThrow(() -> new ResourceNotFoundException("RendezVous", rendezVousId.toString()));
    }

    private Notification createAndMarkSent(RendezVous rendezVous, TypeNotification type, String message) {
        Notification notification = Notification.builder()
            .rendezVous(rendezVous)
            .type(type)
            .message(message)
            .dateEnvoi(LocalDateTime.now())
            .statut(StatutNotification.ENVOYEE)
            .build();

        return notificationRepository.save(notification);
    }

    private NotificationResponse toResponse(Notification notification) {
        RendezVous rendezVous = notification.getRendezVous();

        return NotificationResponse.builder()
            .id(notification.getId())
            .rendezVous(RendezVousResponse.builder()
                .id(rendezVous.getId())
                .patientId(rendezVous.getPatient().getId())
                .patientNom(rendezVous.getPatient().getNom())
                .medecinId(rendezVous.getMedecin().getId())
                .medecinNom(rendezVous.getMedecin().getNom())
                .dateHeure(rendezVous.getDateHeure())
                .motif(rendezVous.getMotif())
                .statut(rendezVous.getStatut())
                .build())
            .type(notification.getType())
            .message(notification.getMessage())
            .dateEnvoi(notification.getDateEnvoi())
            .statut(notification.getStatut())
            .build();
    }
}
