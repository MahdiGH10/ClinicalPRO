package com.clinicpro.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clinicpro.entity.Notification;
import com.clinicpro.entity.StatutNotification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByStatut(StatutNotification statut);

    List<Notification> findByRendezVous_Id(UUID rendezVousId);
}
