package com.clinicpro.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clinicpro.dto.response.ConsultationResponse;
import com.clinicpro.dto.response.FactureResponse;
import com.clinicpro.dto.response.RendezVousResponse;
import com.clinicpro.entity.Consultation;
import com.clinicpro.entity.Facture;
import com.clinicpro.entity.RendezVous;
import com.clinicpro.entity.StatutPaiement;
import com.clinicpro.exception.DuplicateResourceException;
import com.clinicpro.exception.ResourceNotFoundException;
import com.clinicpro.repository.ConsultationRepository;
import com.clinicpro.repository.FactureRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class FactureService {

    private final FactureRepository factureRepository;
    private final ConsultationRepository consultationRepository;

    @Transactional(readOnly = true)
    public List<FactureResponse> findAll() {
        return factureRepository.findAll().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FactureResponse findById(UUID id) {
        Facture facture = factureRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Facture", id.toString()));
        return toResponse(facture);
    }

    @Transactional(readOnly = true)
    public FactureResponse findByConsultationId(UUID consultationId) {
        Facture facture = factureRepository.findByConsultation_Id(consultationId)
            .orElseThrow(() -> new ResourceNotFoundException("Facture", consultationId.toString()));
        return toResponse(facture);
    }

    @Transactional(readOnly = true)
    public List<FactureResponse> findByStatutPaiement(StatutPaiement statutPaiement) {
        return factureRepository.findByStatutPaiement(statutPaiement).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public FactureResponse generateForConsultationResponse(UUID consultationId) {
        return toResponse(resolveOrCreateFacture(consultationId));
    }

    public Facture generateForConsultation(UUID consultationId) {
        return resolveOrCreateFacture(consultationId);
    }

    public FactureResponse markPaidResponse(UUID factureId) {
        Facture facture = factureRepository.findById(factureId)
            .orElseThrow(() -> new ResourceNotFoundException("Facture", factureId.toString()));
        facture.setStatutPaiement(StatutPaiement.PAYEE);
        return toResponse(factureRepository.save(facture));
    }

    public Facture markPaid(UUID factureId) {
        Facture facture = factureRepository.findById(factureId)
            .orElseThrow(() -> new ResourceNotFoundException("Facture", factureId.toString()));
        facture.setStatutPaiement(StatutPaiement.PAYEE);
        return factureRepository.save(facture);
    }

    private Facture resolveOrCreateFacture(UUID consultationId) {
        return factureRepository.findByConsultation_Id(consultationId)
            .orElseGet(() -> createNewFacture(consultationId));
    }

    private Facture createNewFacture(UUID consultationId) {
        Consultation consultation = consultationRepository.findById(consultationId)
            .orElseThrow(() -> new ResourceNotFoundException("Consultation", consultationId.toString()));

        if (consultation.getPrix() == null) {
            throw new DuplicateResourceException("La consultation doit contenir un prix valide pour générer une facture");
        }

        LocalDateTime now = LocalDateTime.now();
        long monthlyCount = factureRepository.countByYearAndMonth(now.getYear(), now.getMonthValue()) + 1;
        String numeroFacture = String.format("FAC-%d%02d-%05d", now.getYear(), now.getMonthValue(), monthlyCount);

        Facture facture = Facture.builder()
            .consultation(consultation)
            .numeroFacture(numeroFacture)
            .montant(consultation.getPrix())
            .dateEmission(now)
            .statutPaiement(StatutPaiement.EN_ATTENTE)
            .build();

        return factureRepository.save(facture);
    }

    private FactureResponse toResponse(Facture facture) {
        Consultation consultation = facture.getConsultation();
        RendezVous rendezVous = consultation.getRendezVous();

        return FactureResponse.builder()
            .id(facture.getId())
            .numeroFacture(facture.getNumeroFacture())
            .consultation(ConsultationResponse.builder()
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
                .build())
            .montant(facture.getMontant())
            .dateEmission(facture.getDateEmission())
            .statutPaiement(facture.getStatutPaiement())
            .build();
    }
}
