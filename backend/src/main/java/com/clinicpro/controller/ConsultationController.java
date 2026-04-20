package com.clinicpro.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinicpro.dto.request.ConsultationRequest;
import com.clinicpro.dto.response.ApiResponse;
import com.clinicpro.dto.response.ConsultationResponse;
import com.clinicpro.service.ConsultationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/consultations")
@RequiredArgsConstructor
public class ConsultationController {

    private final ConsultationService consultationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ConsultationResponse>>> findAll() {
        List<ConsultationResponse> data = consultationService.findAll();
        return ResponseEntity.ok(ApiResponse.ok("Liste des consultations récupérée", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ConsultationResponse>> findById(@PathVariable UUID id) {
        ConsultationResponse data = consultationService.findById(id);
        return ResponseEntity.ok(ApiResponse.ok("Consultation récupérée", data));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<ConsultationResponse>>> findByPatient(@PathVariable UUID patientId) {
        List<ConsultationResponse> data = consultationService.findByPatient(patientId);
        return ResponseEntity.ok(ApiResponse.ok("Consultations du patient récupérées", data));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ConsultationResponse>> create(@Valid @RequestBody ConsultationRequest request) {
        ConsultationResponse data = consultationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Consultation créée avec succès", data));
    }
}
