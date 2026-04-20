package com.clinicpro.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clinicpro.dto.request.RendezVousRequest;
import com.clinicpro.dto.response.RendezVousResponse;
import com.clinicpro.entity.Medecin;
import com.clinicpro.entity.Patient;
import com.clinicpro.entity.RendezVous;
import com.clinicpro.exception.DuplicateResourceException;
import com.clinicpro.exception.ResourceNotFoundException;
import com.clinicpro.repository.MedecinRepository;
import com.clinicpro.repository.PatientRepository;
import com.clinicpro.repository.RendezVousRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RendezVousService {

    private final RendezVousRepository rendezVousRepository;
    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;

    @Transactional(readOnly = true)
    public List<RendezVousResponse> findAll() {
        return rendezVousRepository.findAll().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RendezVousResponse findById(UUID id) {
        RendezVous rendezVous = rendezVousRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("RendezVous", id.toString()));
        return toResponse(rendezVous);
    }

    @Transactional(readOnly = true)
    public List<RendezVousResponse> findByPatient(UUID patientId) {
        return rendezVousRepository.findByPatient_Id(patientId).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public RendezVousResponse create(RendezVousRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
            .orElseThrow(() -> new ResourceNotFoundException("Patient", request.getPatientId().toString()));
        Medecin medecin = medecinRepository.findById(request.getMedecinId())
            .orElseThrow(() -> new ResourceNotFoundException("Medecin", request.getMedecinId().toString()));

        if (rendezVousRepository.existsByMedecin_IdAndDateHeure(request.getMedecinId(), request.getDateHeure())) {
            throw new DuplicateResourceException("Le médecin a déjà un rendez-vous à cette date et heure");
        }

        RendezVous rendezVous = RendezVous.builder()
            .patient(patient)
            .medecin(medecin)
            .dateHeure(request.getDateHeure())
            .motif(request.getMotif())
            .statut(request.getStatut())
            .build();

        return toResponse(rendezVousRepository.save(rendezVous));
    }

    public RendezVousResponse update(UUID id, RendezVousRequest request) {
        RendezVous rendezVous = rendezVousRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("RendezVous", id.toString()));

        Patient patient = patientRepository.findById(request.getPatientId())
            .orElseThrow(() -> new ResourceNotFoundException("Patient", request.getPatientId().toString()));
        Medecin medecin = medecinRepository.findById(request.getMedecinId())
            .orElseThrow(() -> new ResourceNotFoundException("Medecin", request.getMedecinId().toString()));

        if (rendezVousRepository.existsByMedecin_IdAndDateHeureAndIdNot(request.getMedecinId(), request.getDateHeure(), id)) {
            throw new DuplicateResourceException("Le médecin a déjà un rendez-vous à cette date et heure");
        }

        rendezVous.setPatient(patient);
        rendezVous.setMedecin(medecin);
        rendezVous.setDateHeure(request.getDateHeure());
        rendezVous.setMotif(request.getMotif());
        rendezVous.setStatut(request.getStatut());

        return toResponse(rendezVousRepository.save(rendezVous));
    }

    public void delete(UUID id) {
        if (!rendezVousRepository.existsById(id)) {
            throw new ResourceNotFoundException("RendezVous", id.toString());
        }
        rendezVousRepository.deleteById(id);
    }

    private RendezVousResponse toResponse(RendezVous rendezVous) {
        return RendezVousResponse.builder()
            .id(rendezVous.getId())
            .patientId(rendezVous.getPatient().getId())
            .patientNom(rendezVous.getPatient().getNom())
            .medecinId(rendezVous.getMedecin().getId())
            .medecinNom(rendezVous.getMedecin().getNom())
            .dateHeure(rendezVous.getDateHeure())
            .motif(rendezVous.getMotif())
            .statut(rendezVous.getStatut())
            .build();
    }
}
