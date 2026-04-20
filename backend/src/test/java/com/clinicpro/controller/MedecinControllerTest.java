package com.clinicpro.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.clinicpro.dto.request.MedecinRequest;
import com.clinicpro.dto.response.MedecinResponse;
import com.clinicpro.security.JwtAuthFilter;
import com.clinicpro.service.MedecinService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = MedecinController.class)
@AutoConfigureMockMvc(addFilters = false)
class MedecinControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MedecinService medecinService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void getAllShouldReturnOkEnvelope() throws Exception {
        var medecin = MedecinResponse.builder()
            .id(UUID.randomUUID())
            .nom("Dr Ali")
            .specialite("Cardiologie")
            .email("ali@example.com")
            .tel("0600000001")
            .build();

        when(medecinService.findAll()).thenReturn(List.of(medecin));

        mockMvc.perform(get("/api/v1/medecins"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].nom").value("Dr Ali"));
    }

    @Test
    void createShouldReturnCreated() throws Exception {
        MedecinRequest request = MedecinRequest.builder()
            .nom("Dr Ali")
            .specialite("Cardiologie")
            .email("ali@example.com")
            .tel("0600000001")
            .build();

        var created = MedecinResponse.builder()
            .id(UUID.randomUUID())
            .nom(request.getNom())
            .specialite(request.getSpecialite())
            .email(request.getEmail())
            .tel(request.getTel())
            .build();

        when(medecinService.create(any(MedecinRequest.class))).thenReturn(created);

        mockMvc.perform(post("/api/v1/medecins")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.email").value("ali@example.com"));
    }

    @Test
    void getByIdShouldReturnMedecin() throws Exception {
        UUID id = UUID.randomUUID();
        var medecin = MedecinResponse.builder()
            .id(id)
            .nom("Dr Ali")
            .specialite("Cardiologie")
            .email("ali@example.com")
            .tel("0600000001")
            .build();

        when(medecinService.findById(eq(id))).thenReturn(medecin);

        mockMvc.perform(get("/api/v1/medecins/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(id.toString()));
    }
}