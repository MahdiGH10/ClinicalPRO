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

import com.clinicpro.dto.request.MedecinRequest;
import com.clinicpro.dto.response.ApiResponse;
import com.clinicpro.dto.response.MedecinResponse;
import com.clinicpro.service.MedecinService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/medecins")
@RequiredArgsConstructor
public class MedecinController {

    private final MedecinService medecinService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MedecinResponse>>> getAll() {
        List<MedecinResponse> data = medecinService.findAll();
        return ResponseEntity.ok(ApiResponse.ok("Liste des médecins récupérée", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MedecinResponse>> getById(@PathVariable UUID id) {
        MedecinResponse data = medecinService.findById(id);
        return ResponseEntity.ok(ApiResponse.ok("Médecin récupéré", data));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<MedecinResponse>>> search(@RequestParam String nom) {
        List<MedecinResponse> data = medecinService.searchByNom(nom);
        return ResponseEntity.ok(ApiResponse.ok("Résultat de recherche", data));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MedecinResponse>> create(@Valid @RequestBody MedecinRequest request) {
        MedecinResponse data = medecinService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Médecin créé avec succès", data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MedecinResponse>> update(@PathVariable UUID id,
                                                               @Valid @RequestBody MedecinRequest request) {
        MedecinResponse data = medecinService.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Médecin mis à jour avec succès", data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        medecinService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Médecin supprimé avec succès", null));
    }
}