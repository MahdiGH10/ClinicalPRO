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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinicpro.dto.response.ApiResponse;
import com.clinicpro.dto.response.FactureResponse;
import com.clinicpro.entity.StatutPaiement;
import com.clinicpro.service.FactureService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/factures")
@RequiredArgsConstructor
public class FactureController {

    private final FactureService factureService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<FactureResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok("Liste des factures récupérée", factureService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FactureResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Facture récupérée", factureService.findById(id)));
    }

    @GetMapping("/consultation/{consultationId}")
    public ResponseEntity<ApiResponse<FactureResponse>> findByConsultationId(@PathVariable UUID consultationId) {
        return ResponseEntity.ok(ApiResponse.ok("Facture récupérée", factureService.findByConsultationId(consultationId)));
    }

    @GetMapping("/statut")
    public ResponseEntity<ApiResponse<List<FactureResponse>>> findByStatut(@RequestParam StatutPaiement statut) {
        return ResponseEntity.ok(ApiResponse.ok("Factures filtrées par statut", factureService.findByStatutPaiement(statut)));
    }

    @PostMapping("/consultation/{consultationId}")
    public ResponseEntity<ApiResponse<FactureResponse>> generate(@PathVariable UUID consultationId) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Facture générée avec succès", factureService.generateForConsultationResponse(consultationId)));
    }

    @PatchMapping("/{id}/payer")
    public ResponseEntity<ApiResponse<FactureResponse>> markPaid(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Facture marquée comme payée", factureService.markPaidResponse(id)));
    }
}
