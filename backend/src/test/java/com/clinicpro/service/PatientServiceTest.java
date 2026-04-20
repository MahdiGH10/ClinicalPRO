package com.clinicpro.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.clinicpro.dto.request.PatientRequest;
import com.clinicpro.entity.Patient;
import com.clinicpro.exception.DuplicateResourceException;
import com.clinicpro.exception.ResourceNotFoundException;
import com.clinicpro.repository.PatientRepository;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    @Test
    void createShouldPersistPatientWhenPhoneIsUnique() {
        PatientRequest request = PatientRequest.builder()
            .nom("Ali Test")
            .dateNaissance(LocalDate.of(1990, 1, 1))
            .dossierMedical("RAS")
            .tel("0600000000")
            .build();

        Patient saved = Patient.builder()
            .id(UUID.randomUUID())
            .nom(request.getNom())
            .dateNaissance(request.getDateNaissance())
            .dossierMedical(request.getDossierMedical())
            .tel(request.getTel())
            .build();

        when(patientRepository.existsByTel(request.getTel())).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenReturn(saved);

        var response = patientService.create(request);

        assertThat(response.getId()).isEqualTo(saved.getId());
        assertThat(response.getNom()).isEqualTo("Ali Test");
        assertThat(response.getTel()).isEqualTo("0600000000");
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void createShouldThrowWhenPhoneAlreadyExists() {
        PatientRequest request = PatientRequest.builder()
            .nom("Ali")
            .dateNaissance(LocalDate.of(1990, 1, 1))
            .tel("0600000000")
            .build();

        when(patientRepository.existsByTel("0600000000")).thenReturn(true);

        assertThatThrownBy(() -> patientService.create(request))
            .isInstanceOf(DuplicateResourceException.class)
            .hasMessageContaining("existe déjà");

        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void findByIdShouldThrowWhenPatientNotFound() {
        UUID id = UUID.randomUUID();
        when(patientRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.findById(id))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Patient");
    }
}
