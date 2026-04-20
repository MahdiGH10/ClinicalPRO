package com.clinicpro.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.clinicpro.entity.Medecin;
import com.clinicpro.entity.Notification;
import com.clinicpro.entity.Patient;
import com.clinicpro.entity.RendezVous;
import com.clinicpro.entity.StatutNotification;
import com.clinicpro.entity.StatutRendezVous;
import com.clinicpro.entity.TypeNotification;
import com.clinicpro.exception.ResourceNotFoundException;
import com.clinicpro.repository.NotificationRepository;
import com.clinicpro.repository.RendezVousRepository;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private RendezVousRepository rendezVousRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void sendConfirmationShouldCreateSentNotification() {
        UUID rendezVousId = UUID.randomUUID();
        RendezVous rendezVous = rendezVous(rendezVousId, StatutRendezVous.CONFIRME);
        Notification saved = notification(UUID.randomUUID(), rendezVous, TypeNotification.CONFIRMATION, StatutNotification.ENVOYEE);

        when(rendezVousRepository.findById(rendezVousId)).thenReturn(Optional.of(rendezVous));
        when(notificationRepository.save(any(Notification.class))).thenReturn(saved);

        var response = notificationService.sendConfirmation(rendezVousId);

        assertThat(response.getType()).isEqualTo(TypeNotification.CONFIRMATION);
        assertThat(response.getStatut()).isEqualTo(StatutNotification.ENVOYEE);
        assertThat(response.getMessage()).isNotBlank();
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void sendDailyRemindersShouldCreateOneNotificationPerConfirmedRendezVous() {
        LocalDate targetDate = LocalDate.now().plusDays(1);
        RendezVous rdv1 = rendezVous(UUID.randomUUID(), StatutRendezVous.CONFIRME);
        RendezVous rdv2 = rendezVous(UUID.randomUUID(), StatutRendezVous.CONFIRME);

        when(rendezVousRepository.findByStatutAndDateHeureBetween(any(StatutRendezVous.class), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(List.of(rdv1, rdv2));
        when(notificationRepository.save(any(Notification.class)))
            .thenAnswer(invocation -> {
                Notification n = invocation.getArgument(0, Notification.class);
                n.setId(UUID.randomUUID());
                return n;
            });

        var responses = notificationService.sendDailyReminders(targetDate);

        assertThat(responses).hasSize(2);
        assertThat(responses).allMatch(n -> n.getType() == TypeNotification.RAPPEL);
        verify(notificationRepository, times(2)).save(any(Notification.class));
    }

    @Test
    void findByIdShouldThrowWhenMissing() {
        UUID id = UUID.randomUUID();
        when(notificationRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.findById(id))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Notification");
    }

    @Test
    void findByStatutShouldReturnMappedResponses() {
        RendezVous rendezVous = rendezVous(UUID.randomUUID(), StatutRendezVous.CONFIRME);
        Notification notification = notification(UUID.randomUUID(), rendezVous, TypeNotification.RAPPEL, StatutNotification.ENVOYEE);

        when(notificationRepository.findByStatut(StatutNotification.ENVOYEE)).thenReturn(List.of(notification));

        var responses = notificationService.findByStatut(StatutNotification.ENVOYEE);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getStatut()).isEqualTo(StatutNotification.ENVOYEE);
        assertThat(responses.get(0).getRendezVous().getId()).isEqualTo(rendezVous.getId());
        verify(rendezVousRepository, never()).findById(any());
    }

    private RendezVous rendezVous(UUID id, StatutRendezVous statut) {
        Patient patient = Patient.builder()
            .id(UUID.randomUUID())
            .nom("Ali Test")
            .tel("0600000000")
            .build();

        Medecin medecin = Medecin.builder()
            .id(UUID.randomUUID())
            .nom("Dr Amina")
            .specialite("Cardiologie")
            .email("amina@example.com")
            .tel("0600000001")
            .build();

        return RendezVous.builder()
            .id(id)
            .patient(patient)
            .medecin(medecin)
            .dateHeure(LocalDateTime.now().plusDays(1))
            .motif("Contrôle")
            .statut(statut)
            .build();
    }

    private Notification notification(UUID id,
                                      RendezVous rendezVous,
                                      TypeNotification type,
                                      StatutNotification statut) {
        return Notification.builder()
            .id(id)
            .rendezVous(rendezVous)
            .type(type)
            .message("Message test")
            .dateEnvoi(LocalDateTime.now())
            .statut(statut)
            .build();
    }
}
