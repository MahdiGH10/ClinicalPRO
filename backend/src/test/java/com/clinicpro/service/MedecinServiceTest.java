package com.clinicpro.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.clinicpro.dto.request.MedecinRequest;
import com.clinicpro.entity.Medecin;
import com.clinicpro.exception.DuplicateResourceException;
import com.clinicpro.exception.ResourceNotFoundException;
import com.clinicpro.repository.MedecinRepository;

@ExtendWith(MockitoExtension.class)
class MedecinServiceTest {

    @Mock
    private MedecinRepository medecinRepository;

    @InjectMocks
    private MedecinService medecinService;

    @Test
    void createShouldPersistMedecinWhenContactsAreUnique() {
        MedecinRequest request = MedecinRequest.builder()
            .nom("Dr Ali")
            .specialite("Cardiologie")
            .email("ali@example.com")
            .tel("0600000001")
            .build();

        Medecin saved = Medecin.builder()
            .id(UUID.randomUUID())
            .nom(request.getNom())
            .specialite(request.getSpecialite())
            .email(request.getEmail())
            .tel(request.getTel())
            .build();

        when(medecinRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(medecinRepository.existsByTel(request.getTel())).thenReturn(false);
        when(medecinRepository.save(any(Medecin.class))).thenReturn(saved);

        var response = medecinService.create(request);

        assertThat(response.getId()).isEqualTo(saved.getId());
        assertThat(response.getNom()).isEqualTo("Dr Ali");
        assertThat(response.getSpecialite()).isEqualTo("Cardiologie");
        verify(medecinRepository).save(any(Medecin.class));
    }

    @Test
    void createShouldThrowWhenEmailAlreadyExists() {
        MedecinRequest request = MedecinRequest.builder()
            .nom("Dr Ali")
            .specialite("Cardiologie")
            .email("ali@example.com")
            .tel("0600000001")
            .build();

        when(medecinRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> medecinService.create(request))
            .isInstanceOf(DuplicateResourceException.class)
            .hasMessageContaining("email");

        verify(medecinRepository, never()).save(any(Medecin.class));
    }

    @Test
    void findByIdShouldThrowWhenMedecinNotFound() {
        UUID id = UUID.randomUUID();
        when(medecinRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> medecinService.findById(id))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Medecin");
    }

    @Test
    void searchByNomShouldReturnMatchingResults() {
        Medecin medecin = Medecin.builder()
            .id(UUID.randomUUID())
            .nom("Dr Amina")
            .specialite("Dermatologie")
            .email("amina@example.com")
            .tel("0600000002")
            .build();

        when(medecinRepository.findByNomContainingIgnoreCase("ami")).thenReturn(List.of(medecin));

        var results = medecinService.searchByNom("ami");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getNom()).isEqualTo("Dr Amina");
    }
}