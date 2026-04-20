package com.clinicpro.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinicpro.dto.response.ApiResponse;
import com.clinicpro.dto.response.FactureResponse;
import com.clinicpro.service.FactureService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/factures")
@RequiredArgsConstructor
public class FactureController {

    private final FactureService factureService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<FactureResponse>>> findAll() {
        List<FactureResponse> data = factureService.findAll();
        return ResponseEntity.ok(ApiResponse.ok("Liste des factures récupérée", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FactureResponse>> findById(@PathVariable UUID id) {
        FactureResponse data = factureService.findById(id);
        return ResponseEntity.ok(ApiResponse.ok("Facture récupérée", data));
    }

    @GetMapping("/consultation/{consultationId}")
    public ResponseEntity<ApiResponse<FactureResponse>> findByConsultationId(@PathVariable UUID consultationId) {
        FactureResponse data = factureService.findByConsultationId(consultationId);
        return ResponseEntity.ok(ApiResponse.ok("Facture récupérée", data));
    }

    @PostMapping("/consultation/{consultationId}")
    public ResponseEntity<ApiResponse<FactureResponse>> generate(@PathVariable UUID consultationId) {
        FactureResponse data = factureService.generateForConsultation(consultationId);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Facture générée avec succès", data));
    }

    @PatchMapping("/{id}/payer")
    public ResponseEntity<ApiResponse<FactureResponse>> markPaid(@PathVariable UUID id) {
        FactureResponse data = factureService.markPaid(id);
        return ResponseEntity.ok(ApiResponse.ok("Facture marquée comme payée", data));
    }
}
