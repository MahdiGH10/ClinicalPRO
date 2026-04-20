package com.clinicpro.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.clinicpro.dto.request.ConsultationRequest;
import com.clinicpro.entity.Consultation;
import com.clinicpro.entity.Medecin;
import com.clinicpro.entity.Patient;
import com.clinicpro.entity.RendezVous;
import com.clinicpro.entity.StatutRendezVous;
import com.clinicpro.exception.DuplicateResourceException;
import com.clinicpro.exception.ResourceNotFoundException;
import com.clinicpro.repository.ConsultationRepository;
import com.clinicpro.repository.RendezVousRepository;

@ExtendWith(MockitoExtension.class)
class ConsultationServiceTest {

    @Mock
    private ConsultationRepository consultationRepository;

    @Mock
    private RendezVousRepository rendezVousRepository;

    @Mock
    private FactureService factureService;

    @InjectMocks
    private ConsultationService consultationService;

    @Test
    void createShouldPersistConsultationAndMarkRendezVousAsTermine() {
        UUID patientId = UUID.randomUUID();
        UUID medecinId = UUID.randomUUID();
        UUID rendezVousId = UUID.randomUUID();

        Patient patient = Patient.builder().id(patientId).nom("Ali Test").tel("0600000000").build();
        Medecin medecin = Medecin.builder().id(medecinId).nom("Dr Amina").specialite("Cardiologie").email("amina@example.com").tel("0600000001").build();
        RendezVous rendezVous = RendezVous.builder()
            .id(rendezVousId)
            .patient(patient)
            .medecin(medecin)
            .dateHeure(LocalDateTime.now().plusDays(1))
            .motif("Consultation")
            .statut(StatutRendezVous.CONFIRME)
            .build();

        ConsultationRequest request = ConsultationRequest.builder()
            .rendezVousId(rendezVousId)
            .diagnostic("Grippe")
            .ordonnance("Repos")
            .prix(new BigDecimal("150.00"))
            .build();

        Consultation saved = Consultation.builder()
            .id(UUID.randomUUID())
            .rendezVous(rendezVous)
            .diagnostic("Grippe")
            .ordonnance("Repos")
            .prix(new BigDecimal("150.00"))
            .build();

        when(rendezVousRepository.findById(rendezVousId)).thenReturn(Optional.of(rendezVous));
        when(consultationRepository.existsByRendezVous_Id(rendezVousId)).thenReturn(false);
        when(consultationRepository.save(any(Consultation.class))).thenReturn(saved);
        when(rendezVousRepository.save(any(RendezVous.class))).thenReturn(rendezVous);
        when(factureService.generateForConsultation(saved.getId())).thenReturn(null);

        var response = consultationService.create(request);

        assertThat(response.getId()).isEqualTo(saved.getId());
        assertThat(response.getRendezVous().getId()).isEqualTo(rendezVousId);
        assertThat(response.getRendezVous().getPatientNom()).isEqualTo("Ali Test");
        assertThat(rendezVous.getStatut()).isEqualTo(StatutRendezVous.TERMINE);
        verify(rendezVousRepository).save(rendezVous);
        verify(factureService).generateForConsultation(saved.getId());
    }

    @Test
    void createShouldThrowWhenConsultationAlreadyExists() {
        UUID rendezVousId = UUID.randomUUID();
        RendezVous rendezVous = RendezVous.builder().id(rendezVousId).statut(StatutRendezVous.CONFIRME).patient(Patient.builder().id(UUID.randomUUID()).nom("Ali").tel("0600").build()).medecin(Medecin.builder().id(UUID.randomUUID()).nom("Dr").specialite("Cardio").email("d@x.com").tel("0601").build()).build();
        ConsultationRequest request = ConsultationRequest.builder()
            .rendezVousId(rendezVousId)
            .diagnostic("Grippe")
            .ordonnance("Repos")
            .prix(new BigDecimal("150.00"))
            .build();

        when(rendezVousRepository.findById(rendezVousId)).thenReturn(Optional.of(rendezVous));
        when(consultationRepository.existsByRendezVous_Id(rendezVousId)).thenReturn(true);

        assertThatThrownBy(() -> consultationService.create(request))
            .isInstanceOf(DuplicateResourceException.class)
            .hasMessageContaining("déjà");

        verify(consultationRepository, never()).save(any(Consultation.class));
        verify(factureService, never()).generateForConsultation(any());
    }

    @Test
    void findByIdShouldThrowWhenConsultationNotFound() {
        UUID id = UUID.randomUUID();
        when(consultationRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> consultationService.findById(id))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Consultation");
    }

    @Test
    void findByPatientShouldReturnResults() {
        UUID patientId = UUID.randomUUID();
        Patient patient = Patient.builder().id(patientId).nom("Ali Test").tel("0600000000").build();
        Medecin medecin = Medecin.builder().id(UUID.randomUUID()).nom("Dr Amina").specialite("Cardiologie").email("amina@example.com").tel("0600000001").build();
        RendezVous rendezVous = RendezVous.builder()
            .id(UUID.randomUUID())
            .patient(patient)
            .medecin(medecin)
            .dateHeure(LocalDateTime.now().plusDays(1))
            .motif("Consultation")
            .statut(StatutRendezVous.TERMINE)
            .build();
        Consultation consultation = Consultation.builder()
            .id(UUID.randomUUID())
            .rendezVous(rendezVous)
            .diagnostic("Ok")
            .ordonnance("Repos")
            .prix(new BigDecimal("150.00"))
            .build();

        when(consultationRepository.findByRendezVous_Patient_Id(patientId)).thenReturn(List.of(consultation));

        var results = consultationService.findByPatient(patientId);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getRendezVous().getPatientNom()).isEqualTo("Ali Test");
    }
}
