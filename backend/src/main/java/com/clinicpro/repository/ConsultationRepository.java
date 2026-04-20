package com.clinicpro.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clinicpro.entity.Consultation;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, UUID> {

    boolean existsByRendezVous_Id(UUID rendezVousId);

    List<Consultation> findByRendezVous_Patient_Id(UUID patientId);
}
