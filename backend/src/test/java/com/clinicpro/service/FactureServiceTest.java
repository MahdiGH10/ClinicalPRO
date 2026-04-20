package com.clinicpro.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.clinicpro.entity.Consultation;
import com.clinicpro.entity.Facture;
import com.clinicpro.entity.Medecin;
import com.clinicpro.entity.Patient;
import com.clinicpro.entity.RendezVous;
import com.clinicpro.entity.StatutPaiement;
import com.clinicpro.entity.StatutRendezVous;
import com.clinicpro.exception.ResourceNotFoundException;
import com.clinicpro.repository.ConsultationRepository;
import com.clinicpro.repository.FactureRepository;

@ExtendWith(MockitoExtension.class)
class FactureServiceTest {

    @Mock
    private FactureRepository factureRepository;

    @Mock
    private ConsultationRepository consultationRepository;

    @InjectMocks
    private FactureService factureService;

    @Test
    void generateShouldCreateFactureWhenMissing() {
        UUID consultationId = UUID.randomUUID();
        Patient patient = Patient.builder().id(UUID.randomUUID()).nom("Ali Test").tel("0600000000").build();
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
            .id(consultationId)
            .rendezVous(rendezVous)
            .diagnostic("Grippe")
            .ordonnance("Repos")
            .prix(new BigDecimal("150.00"))
            .build();
        Facture saved = Facture.builder()
            .id(UUID.randomUUID())
            .consultation(consultation)
            .numeroFacture("FAC-202604-00008")
            .montant(new BigDecimal("150.00"))
            .dateEmission(LocalDateTime.now())
            .statutPaiement(StatutPaiement.EN_ATTENTE)
            .build();

        when(factureRepository.findByConsultation_Id(consultationId)).thenReturn(Optional.empty());
        when(consultationRepository.findById(consultationId)).thenReturn(Optional.of(consultation));
        when(factureRepository.countByYearAndMonth(any(Integer.class), any(Integer.class))).thenReturn(7L);
        when(factureRepository.save(any(Facture.class))).thenReturn(saved);

        var response = factureService.generateForConsultation(consultationId);

        assertThat(response.getId()).isEqualTo(saved.getId());
        assertThat(response.getNumeroFacture()).startsWith("FAC-");
        assertThat(response.getMontant()).isEqualByComparingTo("150.00");
        verify(factureRepository).save(any(Facture.class));
    }

    @Test
    void generateShouldReturnExistingFactureWhenPresent() {
        UUID consultationId = UUID.randomUUID();
        Patient patient = Patient.builder().id(UUID.randomUUID()).nom("Ali Test").tel("0600000000").build();
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
            .id(consultationId)
            .rendezVous(rendezVous)
            .diagnostic("Grippe")
            .ordonnance("Repos")
            .prix(new BigDecimal("150.00"))
            .build();
        Facture existing = Facture.builder()
            .id(UUID.randomUUID())
            .consultation(consultation)
            .numeroFacture("FAC-202604-00001")
            .montant(new BigDecimal("150.00"))
            .dateEmission(LocalDateTime.now())
            .statutPaiement(StatutPaiement.EN_ATTENTE)
            .build();

        when(factureRepository.findByConsultation_Id(consultationId)).thenReturn(Optional.of(existing));

        var response = factureService.generateForConsultation(consultationId);

        assertThat(response.getId()).isEqualTo(existing.getId());
        verify(consultationRepository, never()).findById(any());
        verify(factureRepository, never()).save(any(Facture.class));
    }

    @Test
    void findByIdShouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(factureRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> factureService.findById(id))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Facture");
    }

    @Test
    void markPaidShouldUpdateStatus() {
        UUID id = UUID.randomUUID();
        Facture facture = Facture.builder()
            .id(id)
            .numeroFacture("FAC-202604-00001")
            .montant(new BigDecimal("150.00"))
            .dateEmission(LocalDateTime.now())
            .statutPaiement(StatutPaiement.EN_ATTENTE)
            .consultation(Consultation.builder().id(UUID.randomUUID()).rendezVous(RendezVous.builder().patient(Patient.builder().id(UUID.randomUUID()).nom("Ali").tel("060").build()).medecin(Medecin.builder().id(UUID.randomUUID()).nom("Dr").specialite("Cardio").email("d@x.com").tel("0601").build()).dateHeure(LocalDateTime.now().plusDays(1)).motif("Consult").statut(StatutRendezVous.TERMINE).build()).diagnostic("Ok").ordonnance("Repos").prix(new BigDecimal("150.00")).build())
            .build();

        when(factureRepository.findById(id)).thenReturn(Optional.of(facture));
        when(factureRepository.save(facture)).thenReturn(facture);

        var response = factureService.markPaid(id);

        assertThat(response.getStatutPaiement()).isEqualTo(StatutPaiement.PAYEE);
        verify(factureRepository).save(facture);
    }
}
