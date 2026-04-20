package com.clinicpro.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.clinicpro.dto.request.ConsultationRequest;
import com.clinicpro.dto.response.ConsultationResponse;
import com.clinicpro.dto.response.RendezVousResponse;
import com.clinicpro.entity.StatutRendezVous;
import com.clinicpro.security.JwtAuthFilter;
import com.clinicpro.service.ConsultationService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = ConsultationController.class)
@AutoConfigureMockMvc(addFilters = false)
class ConsultationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ConsultationService consultationService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void getAllShouldReturnOkEnvelope() throws Exception {
        UUID patientId = UUID.randomUUID();
        UUID medecinId = UUID.randomUUID();
        var response = ConsultationResponse.builder()
            .id(UUID.randomUUID())
            .rendezVous(RendezVousResponse.builder()
                .id(UUID.randomUUID())
                .patientId(patientId)
                .patientNom("Ali Test")
                .medecinId(medecinId)
                .medecinNom("Dr Amina")
                .dateHeure(LocalDateTime.now().plusDays(1))
                .motif("Consultation")
                .statut(StatutRendezVous.TERMINE)
                .build())
            .diagnostic("Grippe")
            .ordonnance("Repos")
            .prix(new BigDecimal("150.00"))
            .build();

        when(consultationService.findAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/consultations"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].diagnostic").value("Grippe"));
    }

    @Test
    void createShouldReturnCreated() throws Exception {
        UUID rendezVousId = UUID.randomUUID();
        ConsultationRequest request = ConsultationRequest.builder()
            .rendezVousId(rendezVousId)
            .diagnostic("Grippe")
            .ordonnance("Repos")
            .prix(new BigDecimal("150.00"))
            .build();

        var created = ConsultationResponse.builder()
            .id(UUID.randomUUID())
            .rendezVous(RendezVousResponse.builder()
                .id(rendezVousId)
                .patientId(UUID.randomUUID())
                .patientNom("Ali Test")
                .medecinId(UUID.randomUUID())
                .medecinNom("Dr Amina")
                .dateHeure(LocalDateTime.now().plusDays(1))
                .motif("Consultation")
                .statut(StatutRendezVous.TERMINE)
                .build())
            .diagnostic("Grippe")
            .ordonnance("Repos")
            .prix(new BigDecimal("150.00"))
            .build();

        when(consultationService.create(any(ConsultationRequest.class))).thenReturn(created);

        mockMvc.perform(post("/api/v1/consultations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.diagnostic").value("Grippe"));
    }

    @Test
    void findByIdShouldReturnConsultation() throws Exception {
        UUID id = UUID.randomUUID();
        var response = ConsultationResponse.builder()
            .id(id)
            .rendezVous(RendezVousResponse.builder()
                .id(UUID.randomUUID())
                .patientId(UUID.randomUUID())
                .patientNom("Ali Test")
                .medecinId(UUID.randomUUID())
                .medecinNom("Dr Amina")
                .dateHeure(LocalDateTime.now().plusDays(1))
                .motif("Consultation")
                .statut(StatutRendezVous.TERMINE)
                .build())
            .diagnostic("Grippe")
            .ordonnance("Repos")
            .prix(new BigDecimal("150.00"))
            .build();

        when(consultationService.findById(eq(id))).thenReturn(response);

        mockMvc.perform(get("/api/v1/consultations/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(id.toString()));
    }
}
