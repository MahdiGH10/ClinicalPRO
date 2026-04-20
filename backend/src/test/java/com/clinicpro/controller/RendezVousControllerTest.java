package com.clinicpro.controller;

import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.clinicpro.dto.request.RendezVousRequest;
import com.clinicpro.dto.response.RendezVousResponse;
import com.clinicpro.entity.StatutRendezVous;
import com.clinicpro.security.JwtAuthFilter;
import com.clinicpro.service.RendezVousService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = RendezVousController.class)
@AutoConfigureMockMvc(addFilters = false)
class RendezVousControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RendezVousService rendezVousService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void getAllShouldReturnOkEnvelope() throws Exception {
        UUID patientId = UUID.randomUUID();
        UUID medecinId = UUID.randomUUID();

        var rendezVous = RendezVousResponse.builder()
            .id(UUID.randomUUID())
            .patientId(patientId)
            .patientNom("Ali Test")
            .medecinId(medecinId)
            .medecinNom("Dr Amina")
            .dateHeure(LocalDateTime.now().plusDays(1))
            .motif("Consultation")
            .statut(StatutRendezVous.EN_ATTENTE)
            .build();

        when(rendezVousService.findAll()).thenReturn(List.of(rendezVous));

        mockMvc.perform(get("/api/v1/rendezvous"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].patientNom").value("Ali Test"));
    }

    @Test
    void createShouldReturnCreated() throws Exception {
        UUID patientId = UUID.randomUUID();
        UUID medecinId = UUID.randomUUID();
        LocalDateTime whenDate = LocalDateTime.now().plusDays(1);

        RendezVousRequest request = RendezVousRequest.builder()
            .patientId(patientId)
            .medecinId(medecinId)
            .dateHeure(whenDate)
            .motif("Consultation")
            .statut(StatutRendezVous.EN_ATTENTE)
            .build();

        var created = RendezVousResponse.builder()
            .id(UUID.randomUUID())
            .patientId(patientId)
            .patientNom("Ali Test")
            .medecinId(medecinId)
            .medecinNom("Dr Amina")
            .dateHeure(whenDate)
            .motif("Consultation")
            .statut(StatutRendezVous.EN_ATTENTE)
            .build();

        when(rendezVousService.create(any(RendezVousRequest.class))).thenReturn(created);

        mockMvc.perform(post("/api/v1/rendezvous")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.medecinNom").value("Dr Amina"));
    }

    @Test
    void getByIdShouldReturnRendezVous() throws Exception {
        UUID id = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();
        UUID medecinId = UUID.randomUUID();

        var response = RendezVousResponse.builder()
            .id(id)
            .patientId(patientId)
            .patientNom("Ali Test")
            .medecinId(medecinId)
            .medecinNom("Dr Amina")
            .dateHeure(LocalDateTime.now().plusDays(1))
            .motif("Consultation")
            .statut(StatutRendezVous.CONFIRME)
            .build();

        when(rendezVousService.findById(eq(id))).thenReturn(response);

        mockMvc.perform(get("/api/v1/rendezvous/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(id.toString()));
    }
}
