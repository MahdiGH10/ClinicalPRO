package com.clinicpro.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.clinicpro.dto.request.PatientRequest;
import com.clinicpro.dto.response.PatientResponse;
import com.clinicpro.security.JwtAuthFilter;
import com.clinicpro.service.PatientService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = PatientController.class)
@AutoConfigureMockMvc(addFilters = false)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PatientService patientService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void getAllShouldReturnOkEnvelope() throws Exception {
        var patient = PatientResponse.builder()
            .id(UUID.randomUUID())
            .nom("Ali Test")
            .dateNaissance(LocalDate.of(1990, 1, 1))
            .dossierMedical("RAS")
            .tel("0600000000")
            .build();

        when(patientService.findAll()).thenReturn(List.of(patient));

        mockMvc.perform(get("/api/v1/patients"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].nom").value("Ali Test"));
    }

    @Test
    void createShouldReturnCreated() throws Exception {
        PatientRequest request = PatientRequest.builder()
            .nom("Ali Test")
            .dateNaissance(LocalDate.of(1990, 1, 1))
            .dossierMedical("RAS")
            .tel("0600000000")
            .build();

        var created = PatientResponse.builder()
            .id(UUID.randomUUID())
            .nom(request.getNom())
            .dateNaissance(request.getDateNaissance())
            .dossierMedical(request.getDossierMedical())
            .tel(request.getTel())
            .build();

        when(patientService.create(any(PatientRequest.class))).thenReturn(created);

        mockMvc.perform(post("/api/v1/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.nom").value("Ali Test"))
            .andExpect(jsonPath("$.data.tel").value("0600000000"));
    }

    @Test
    void getByIdShouldReturnPatient() throws Exception {
        UUID id = UUID.randomUUID();
        var patient = PatientResponse.builder()
            .id(id)
            .nom("Ali Test")
            .dateNaissance(LocalDate.of(1990, 1, 1))
            .dossierMedical("RAS")
            .tel("0600000000")
            .build();

        when(patientService.findById(eq(id))).thenReturn(patient);

        mockMvc.perform(get("/api/v1/patients/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(id.toString()));
    }
}
