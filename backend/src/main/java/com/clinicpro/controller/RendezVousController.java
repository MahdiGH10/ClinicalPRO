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

import com.clinicpro.dto.request.RendezVousRequest;
import com.clinicpro.dto.response.ApiResponse;
import com.clinicpro.dto.response.RendezVousResponse;
import com.clinicpro.service.RendezVousService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/rendezvous")
@RequiredArgsConstructor
public class RendezVousController {

    private final RendezVousService rendezVousService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RendezVousResponse>>> getAll() {
        List<RendezVousResponse> data = rendezVousService.findAll();
        return ResponseEntity.ok(ApiResponse.ok("Liste des rendez-vous récupérée", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RendezVousResponse>> getById(@PathVariable UUID id) {
        RendezVousResponse data = rendezVousService.findById(id);
        return ResponseEntity.ok(ApiResponse.ok("Rendez-vous récupéré", data));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<RendezVousResponse>>> searchByPatient(@RequestParam UUID patientId) {
        List<RendezVousResponse> data = rendezVousService.findByPatient(patientId);
        return ResponseEntity.ok(ApiResponse.ok("Résultat de recherche", data));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RendezVousResponse>> create(@Valid @RequestBody RendezVousRequest request) {
        RendezVousResponse data = rendezVousService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Rendez-vous créé avec succès", data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RendezVousResponse>> update(@PathVariable UUID id,
                                                                  @Valid @RequestBody RendezVousRequest request) {
        RendezVousResponse data = rendezVousService.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Rendez-vous mis à jour avec succès", data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        rendezVousService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Rendez-vous supprimé avec succès", null));
    }
}
