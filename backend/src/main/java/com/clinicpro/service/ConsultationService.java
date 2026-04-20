package com.clinicpro.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clinicpro.dto.request.ConsultationRequest;
import com.clinicpro.dto.response.ConsultationResponse;
import com.clinicpro.dto.response.RendezVousResponse;
import com.clinicpro.entity.Consultation;
import com.clinicpro.entity.RendezVous;
import com.clinicpro.entity.StatutRendezVous;
import com.clinicpro.exception.DuplicateResourceException;
import com.clinicpro.exception.ResourceNotFoundException;
import com.clinicpro.repository.ConsultationRepository;
import com.clinicpro.repository.RendezVousRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final RendezVousRepository rendezVousRepository;
    private final FactureService factureService;

    @Transactional(readOnly = true)
    public List<ConsultationResponse> findAll() {
        return consultationRepository.findAll().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ConsultationResponse findById(UUID id) {
        Consultation consultation = consultationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Consultation", id.toString()));
        return toResponse(consultation);
    }

    @Transactional(readOnly = true)
    public List<ConsultationResponse> findByPatient(UUID patientId) {
        return consultationRepository.findByRendezVous_Patient_Id(patientId).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public ConsultationResponse create(ConsultationRequest request) {
        RendezVous rendezVous = rendezVousRepository.findById(request.getRendezVousId())
            .orElseThrow(() -> new ResourceNotFoundException("RendezVous", request.getRendezVousId().toString()));

        if (consultationRepository.existsByRendezVous_Id(request.getRendezVousId())) {
            throw new DuplicateResourceException("Une consultation existe déjà pour ce rendez-vous");
        }

        Consultation consultation = Consultation.builder()
            .rendezVous(rendezVous)
            .diagnostic(request.getDiagnostic())
            .ordonnance(request.getOrdonnance())
            .prix(request.getPrix())
            .build();

        Consultation saved = consultationRepository.save(consultation);

        if (rendezVous.getStatut() != StatutRendezVous.TERMINE) {
            rendezVous.setStatut(StatutRendezVous.TERMINE);
            rendezVousRepository.save(rendezVous);
        }

        factureService.generateForConsultation(saved.getId());

        return toResponse(saved);
    }

    private ConsultationResponse toResponse(Consultation consultation) {
        RendezVous rendezVous = consultation.getRendezVous();

        return ConsultationResponse.builder()
            .id(consultation.getId())
            .rendezVous(RendezVousResponse.builder()
                .id(rendezVous.getId())
                .patientId(rendezVous.getPatient().getId())
                .patientNom(rendezVous.getPatient().getNom())
                .medecinId(rendezVous.getMedecin().getId())
                .medecinNom(rendezVous.getMedecin().getNom())
                .dateHeure(rendezVous.getDateHeure())
                .motif(rendezVous.getMotif())
                .statut(rendezVous.getStatut())
                .build())
            .diagnostic(consultation.getDiagnostic())
            .ordonnance(consultation.getOrdonnance())
            .prix(consultation.getPrix())
            .build();
    }
}
