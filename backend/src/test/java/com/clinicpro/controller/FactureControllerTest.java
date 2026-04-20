package com.clinicpro.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
import com.clinicpro.dto.response.FactureResponse;
import com.clinicpro.dto.response.RendezVousResponse;
import com.clinicpro.entity.StatutPaiement;
import com.clinicpro.entity.StatutRendezVous;
import com.clinicpro.security.JwtAuthFilter;
import com.clinicpro.service.FactureService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = FactureController.class)
@AutoConfigureMockMvc(addFilters = false)
class FactureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FactureService factureService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void getAllShouldReturnOkEnvelope() throws Exception {
        FactureResponse response = FactureResponse.builder()
            .id(UUID.randomUUID())
            .numeroFacture("FAC-202604-00001")
            .consultation(ConsultationResponse.builder()
                .id(UUID.randomUUID())
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
                .build())
            .montant(new BigDecimal("150.00"))
            .dateEmission(LocalDateTime.now())
            .statutPaiement(StatutPaiement.EN_ATTENTE)
            .build();

        when(factureService.findAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/factures"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].numeroFacture").value("FAC-202604-00001"));
    }

    @Test
    void generateShouldReturnCreated() throws Exception {
        UUID consultationId = UUID.randomUUID();
        FactureResponse response = FactureResponse.builder()
            .id(UUID.randomUUID())
            .numeroFacture("FAC-202604-00001")
            .consultation(ConsultationResponse.builder()
                .id(consultationId)
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
                .build())
            .montant(new BigDecimal("150.00"))
            .dateEmission(LocalDateTime.now())
            .statutPaiement(StatutPaiement.EN_ATTENTE)
            .build();

        when(factureService.generateForConsultationResponse(eq(consultationId))).thenReturn(response);

        mockMvc.perform(post("/api/v1/factures/consultation/{consultationId}", consultationId))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.numeroFacture").value("FAC-202604-00001"));
    }

    @Test
    void markPaidShouldReturnUpdatedFacture() throws Exception {
        UUID id = UUID.randomUUID();
        FactureResponse response = FactureResponse.builder()
            .id(id)
            .numeroFacture("FAC-202604-00001")
            .consultation(ConsultationResponse.builder()
                .id(UUID.randomUUID())
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
                .build())
            .montant(new BigDecimal("150.00"))
            .dateEmission(LocalDateTime.now())
            .statutPaiement(StatutPaiement.PAYEE)
            .build();

        when(factureService.markPaidResponse(eq(id))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/factures/{id}/payer", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.statutPaiement").value("PAYEE"));
    }
}
