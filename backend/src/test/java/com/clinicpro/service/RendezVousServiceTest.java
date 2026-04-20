package com.clinicpro.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.clinicpro.dto.request.RendezVousRequest;
import com.clinicpro.entity.Medecin;
import com.clinicpro.entity.Patient;
import com.clinicpro.entity.RendezVous;
import com.clinicpro.entity.StatutRendezVous;
import com.clinicpro.exception.DuplicateResourceException;
import com.clinicpro.exception.ResourceNotFoundException;
import com.clinicpro.repository.MedecinRepository;
import com.clinicpro.repository.PatientRepository;
import com.clinicpro.repository.RendezVousRepository;

@ExtendWith(MockitoExtension.class)
class RendezVousServiceTest {

    @Mock
    private RendezVousRepository rendezVousRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private MedecinRepository medecinRepository;

    @InjectMocks
    private RendezVousService rendezVousService;

    @Test
    void createShouldPersistRendezVousWhenSlotIsAvailable() {
        UUID patientId = UUID.randomUUID();
        UUID medecinId = UUID.randomUUID();
        LocalDateTime whenDate = LocalDateTime.now().plusDays(1);

        Patient patient = Patient.builder().id(patientId).nom("Ali Test").tel("0600000000").build();
        Medecin medecin = Medecin.builder().id(medecinId).nom("Dr Amina").email("a@x.com").tel("0601").specialite("Cardiologie").build();

        RendezVousRequest request = RendezVousRequest.builder()
            .patientId(patientId)
            .medecinId(medecinId)
            .dateHeure(whenDate)
            .motif("Suivi")
            .statut(StatutRendezVous.EN_ATTENTE)
            .build();

        RendezVous saved = RendezVous.builder()
            .id(UUID.randomUUID())
            .patient(patient)
            .medecin(medecin)
            .dateHeure(whenDate)
            .motif("Suivi")
            .statut(StatutRendezVous.EN_ATTENTE)
            .build();

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(medecinRepository.findById(medecinId)).thenReturn(Optional.of(medecin));
        when(rendezVousRepository.existsByMedecin_IdAndDateHeure(medecinId, whenDate)).thenReturn(false);
        when(rendezVousRepository.save(any(RendezVous.class))).thenReturn(saved);

        var response = rendezVousService.create(request);

        assertThat(response.getId()).isEqualTo(saved.getId());
        assertThat(response.getPatientId()).isEqualTo(patientId);
        assertThat(response.getMedecinId()).isEqualTo(medecinId);
        verify(rendezVousRepository).save(any(RendezVous.class));
    }

    @Test
    void createShouldThrowWhenMedecinIsBusy() {
        UUID patientId = UUID.randomUUID();
        UUID medecinId = UUID.randomUUID();
        LocalDateTime whenDate = LocalDateTime.now().plusDays(1);

        Patient patient = Patient.builder().id(patientId).nom("Ali Test").tel("0600000000").build();
        Medecin medecin = Medecin.builder().id(medecinId).nom("Dr Amina").email("a@x.com").tel("0601").specialite("Cardiologie").build();

        RendezVousRequest request = RendezVousRequest.builder()
            .patientId(patientId)
            .medecinId(medecinId)
            .dateHeure(whenDate)
            .motif("Suivi")
            .statut(StatutRendezVous.EN_ATTENTE)
            .build();

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(medecinRepository.findById(medecinId)).thenReturn(Optional.of(medecin));
        when(rendezVousRepository.existsByMedecin_IdAndDateHeure(medecinId, whenDate)).thenReturn(true);

        assertThatThrownBy(() -> rendezVousService.create(request))
            .isInstanceOf(DuplicateResourceException.class)
            .hasMessageContaining("déjà un rendez-vous");

        verify(rendezVousRepository, never()).save(any(RendezVous.class));
    }

    @Test
    void findByIdShouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(rendezVousRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rendezVousService.findById(id))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("RendezVous");
    }

    @Test
    void findByPatientShouldReturnMatchingResults() {
        UUID patientId = UUID.randomUUID();
        UUID medecinId = UUID.randomUUID();

        Patient patient = Patient.builder().id(patientId).nom("Ali Test").tel("0600000000").build();
        Medecin medecin = Medecin.builder().id(medecinId).nom("Dr Amina").email("a@x.com").tel("0601").specialite("Cardiologie").build();

        RendezVous rendezVous = RendezVous.builder()
            .id(UUID.randomUUID())
            .patient(patient)
            .medecin(medecin)
            .dateHeure(LocalDateTime.now().plusDays(2))
            .motif("Contrôle")
            .statut(StatutRendezVous.CONFIRME)
            .build();

        when(rendezVousRepository.findByPatient_Id(patientId)).thenReturn(List.of(rendezVous));

        var result = rendezVousService.findByPatient(patientId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPatientNom()).isEqualTo("Ali Test");
        assertThat(result.get(0).getMedecinNom()).isEqualTo("Dr Amina");
    }
}
