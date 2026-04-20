package com.clinicpro.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clinicpro.dto.request.MedecinRequest;
import com.clinicpro.dto.response.MedecinResponse;
import com.clinicpro.entity.Medecin;
import com.clinicpro.exception.DuplicateResourceException;
import com.clinicpro.exception.ResourceNotFoundException;
import com.clinicpro.repository.MedecinRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MedecinService {

    private final MedecinRepository medecinRepository;

    @Transactional(readOnly = true)
    public List<MedecinResponse> findAll() {
        return medecinRepository.findAll().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MedecinResponse findById(UUID id) {
        Medecin medecin = medecinRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Medecin", id.toString()));
        return toResponse(medecin);
    }

    @Transactional(readOnly = true)
    public List<MedecinResponse> searchByNom(String nom) {
        return medecinRepository.findByNomContainingIgnoreCase(nom).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public MedecinResponse create(MedecinRequest request) {
        ensureUniqueContacts(request.getEmail(), request.getTel(), null);

        Medecin medecin = Medecin.builder()
            .nom(request.getNom())
            .specialite(request.getSpecialite())
            .email(request.getEmail())
            .tel(request.getTel())
            .build();

        return toResponse(medecinRepository.save(medecin));
    }

    public MedecinResponse update(UUID id, MedecinRequest request) {
        Medecin medecin = medecinRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Medecin", id.toString()));

        ensureUniqueContacts(request.getEmail(), request.getTel(), id);

        medecin.setNom(request.getNom());
        medecin.setSpecialite(request.getSpecialite());
        medecin.setEmail(request.getEmail());
        medecin.setTel(request.getTel());

        return toResponse(medecinRepository.save(medecin));
    }

    public void delete(UUID id) {
        if (!medecinRepository.existsById(id)) {
            throw new ResourceNotFoundException("Medecin", id.toString());
        }
        medecinRepository.deleteById(id);
    }

    private void ensureUniqueContacts(String email, String tel, UUID currentId) {
        boolean emailExists = currentId == null
            ? medecinRepository.existsByEmail(email)
            : medecinRepository.existsByEmailAndIdNot(email, currentId);
        if (emailExists) {
            throw new DuplicateResourceException("Un médecin avec cet email existe déjà");
        }

        boolean telExists = currentId == null
            ? medecinRepository.existsByTel(tel)
            : medecinRepository.existsByTelAndIdNot(tel, currentId);
        if (telExists) {
            throw new DuplicateResourceException("Un médecin avec ce numéro de téléphone existe déjà");
        }
    }

    private MedecinResponse toResponse(Medecin medecin) {
        return MedecinResponse.builder()
            .id(medecin.getId())
            .nom(medecin.getNom())
            .specialite(medecin.getSpecialite())
            .email(medecin.getEmail())
            .tel(medecin.getTel())
            .build();
    }
}