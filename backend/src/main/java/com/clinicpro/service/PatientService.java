package com.clinicpro.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clinicpro.dto.request.PatientRequest;
import com.clinicpro.dto.response.PatientResponse;
import com.clinicpro.entity.Patient;
import com.clinicpro.exception.DuplicateResourceException;
import com.clinicpro.exception.ResourceNotFoundException;
import com.clinicpro.repository.PatientRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;

    @Transactional(readOnly = true)
    public List<PatientResponse> findAll() {
        return patientRepository.findAll().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PatientResponse findById(UUID id) {
        Patient patient = patientRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Patient", id.toString()));
        return toResponse(patient);
    }

    public PatientResponse create(PatientRequest request) {
        if (patientRepository.existsByTel(request.getTel())) {
            throw new DuplicateResourceException("Un patient avec ce numéro de téléphone existe déjà");
        }

        Patient patient = Patient.builder()
            .nom(request.getNom())
            .dateNaissance(request.getDateNaissance())
            .dossierMedical(request.getDossierMedical())
            .tel(request.getTel())
            .build();

        return toResponse(patientRepository.save(patient));
    }

    public PatientResponse update(UUID id, PatientRequest request) {
        Patient patient = patientRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Patient", id.toString()));

        if (patientRepository.existsByTelAndIdNot(request.getTel(), id)) {
            throw new DuplicateResourceException("Un autre patient utilise déjà ce numéro de téléphone");
        }

        patient.setNom(request.getNom());
        patient.setDateNaissance(request.getDateNaissance());
        patient.setDossierMedical(request.getDossierMedical());
        patient.setTel(request.getTel());

        return toResponse(patientRepository.save(patient));
    }

    public void delete(UUID id) {
        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Patient", id.toString());
        }
        patientRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<PatientResponse> searchByNom(String nom) {
        return patientRepository.findByNomContainingIgnoreCase(nom).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    private PatientResponse toResponse(Patient patient) {
        return PatientResponse.builder()
            .id(patient.getId())
            .nom(patient.getNom())
            .dateNaissance(patient.getDateNaissance())
            .dossierMedical(patient.getDossierMedical())
            .tel(patient.getTel())
            .build();
    }
}
