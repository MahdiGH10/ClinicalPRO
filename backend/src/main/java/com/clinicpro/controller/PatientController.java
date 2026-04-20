package com.clinicpro.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinicpro.dto.request.PatientRequest;
import com.clinicpro.dto.response.ApiResponse;
import com.clinicpro.dto.response.PatientResponse;
import com.clinicpro.service.PatientService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PatientResponse>>> getAll() {
        List<PatientResponse> data = patientService.findAll();
        return ResponseEntity.ok(ApiResponse.ok("Liste des patients récupérée", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientResponse>> getById(@PathVariable UUID id) {
        PatientResponse data = patientService.findById(id);
        return ResponseEntity.ok(ApiResponse.ok("Patient récupéré", data));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PatientResponse>>> search(@RequestParam String nom) {
        List<PatientResponse> data = patientService.searchByNom(nom);
        return ResponseEntity.ok(ApiResponse.ok("Résultat de recherche", data));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PatientResponse>> create(@Valid @RequestBody PatientRequest request) {
        PatientResponse data = patientService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Patient créé avec succès", data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientResponse>> update(@PathVariable UUID id,
                                                               @Valid @RequestBody PatientRequest request) {
        PatientResponse data = patientService.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Patient mis à jour avec succès", data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        patientService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Patient supprimé avec succès", null));
    }
}
