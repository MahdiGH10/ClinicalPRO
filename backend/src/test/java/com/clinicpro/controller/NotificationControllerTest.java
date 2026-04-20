package com.clinicpro.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.clinicpro.dto.response.NotificationResponse;
import com.clinicpro.dto.response.RendezVousResponse;
import com.clinicpro.entity.StatutNotification;
import com.clinicpro.entity.StatutRendezVous;
import com.clinicpro.entity.TypeNotification;
import com.clinicpro.security.JwtAuthFilter;
import com.clinicpro.service.NotificationService;

@WebMvcTest(controllers = NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void getAllShouldReturnOkEnvelope() throws Exception {
        var notification = notificationResponse(TypeNotification.RAPPEL, StatutNotification.ENVOYEE);

        when(notificationService.findAll()).thenReturn(List.of(notification));

        mockMvc.perform(get("/api/v1/notifications"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].type").value("RAPPEL"));
    }

    @Test
    void findByIdShouldReturnNotification() throws Exception {
        UUID id = UUID.randomUUID();
        var notification = notificationResponse(TypeNotification.CONFIRMATION, StatutNotification.ENVOYEE);
        notification.setId(id);

        when(notificationService.findById(eq(id))).thenReturn(notification);

        mockMvc.perform(get("/api/v1/notifications/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(id.toString()));
    }

    @Test
    void sendConfirmationShouldReturnCreated() throws Exception {
        UUID rendezVousId = UUID.randomUUID();
        var notification = notificationResponse(TypeNotification.CONFIRMATION, StatutNotification.ENVOYEE);

        when(notificationService.sendConfirmation(eq(rendezVousId))).thenReturn(notification);

        mockMvc.perform(post("/api/v1/notifications/confirmation/{rendezVousId}", rendezVousId))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.type").value("CONFIRMATION"));
    }

    @Test
    void sendDailyRemindersShouldReturnCreatedList() throws Exception {
        var notification = notificationResponse(TypeNotification.RAPPEL, StatutNotification.ENVOYEE);

        when(notificationService.sendDailyReminders(eq(null))).thenReturn(List.of(notification));

        mockMvc.perform(post("/api/v1/notifications/rappels"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].statut").value("ENVOYEE"));
    }

    private NotificationResponse notificationResponse(TypeNotification type, StatutNotification statut) {
        return NotificationResponse.builder()
            .id(UUID.randomUUID())
            .rendezVous(RendezVousResponse.builder()
                .id(UUID.randomUUID())
                .patientId(UUID.randomUUID())
                .patientNom("Ali Test")
                .medecinId(UUID.randomUUID())
                .medecinNom("Dr Amina")
                .dateHeure(LocalDateTime.now().plusDays(1))
                .motif("Contrôle")
                .statut(StatutRendezVous.CONFIRME)
                .build())
            .type(type)
            .message("Message test")
            .dateEnvoi(LocalDateTime.now())
            .statut(statut)
            .build();
    }
}
