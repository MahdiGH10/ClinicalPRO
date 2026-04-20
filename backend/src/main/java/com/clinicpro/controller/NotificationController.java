package com.clinicpro.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinicpro.dto.response.ApiResponse;
import com.clinicpro.dto.response.NotificationResponse;
import com.clinicpro.entity.StatutNotification;
import com.clinicpro.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> findAll() {
        List<NotificationResponse> data = notificationService.findAll();
        return ResponseEntity.ok(ApiResponse.ok("Liste des notifications récupérée", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NotificationResponse>> findById(@PathVariable UUID id) {
        NotificationResponse data = notificationService.findById(id);
        return ResponseEntity.ok(ApiResponse.ok("Notification récupérée", data));
    }

    @GetMapping("/rendezvous/{rendezVousId}")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> findByRendezVousId(@PathVariable UUID rendezVousId) {
        List<NotificationResponse> data = notificationService.findByRendezVousId(rendezVousId);
        return ResponseEntity.ok(ApiResponse.ok("Notifications du rendez-vous récupérées", data));
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> findByStatut(@PathVariable StatutNotification statut) {
        List<NotificationResponse> data = notificationService.findByStatut(statut);
        return ResponseEntity.ok(ApiResponse.ok("Notifications filtrées par statut", data));
    }

    @PostMapping("/confirmation/{rendezVousId}")
    public ResponseEntity<ApiResponse<NotificationResponse>> sendConfirmation(@PathVariable UUID rendezVousId) {
        NotificationResponse data = notificationService.sendConfirmation(rendezVousId);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Notification de confirmation envoyée", data));
    }

    @PostMapping("/annulation/{rendezVousId}")
    public ResponseEntity<ApiResponse<NotificationResponse>> sendCancellation(@PathVariable UUID rendezVousId) {
        NotificationResponse data = notificationService.sendCancellation(rendezVousId);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Notification d'annulation envoyée", data));
    }

    @PostMapping("/rappel/{rendezVousId}")
    public ResponseEntity<ApiResponse<NotificationResponse>> sendReminder(@PathVariable UUID rendezVousId) {
        NotificationResponse data = notificationService.sendReminder(rendezVousId);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Notification de rappel envoyée", data));
    }

    @PostMapping("/rappels")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> sendDailyReminders(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<NotificationResponse> data = notificationService.sendDailyReminders(date);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Notifications de rappel quotidiennes envoyées", data));
    }
}
