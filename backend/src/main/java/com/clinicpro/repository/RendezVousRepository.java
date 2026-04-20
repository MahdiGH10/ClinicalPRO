package com.clinicpro.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clinicpro.entity.RendezVous;
import com.clinicpro.entity.StatutRendezVous;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, UUID> {

    List<RendezVous> findByPatient_Id(UUID patientId);

    List<RendezVous> findByMedecin_Id(UUID medecinId);

    boolean existsByMedecin_IdAndDateHeure(UUID medecinId, LocalDateTime dateHeure);

    boolean existsByMedecin_IdAndDateHeureAndIdNot(UUID medecinId, LocalDateTime dateHeure, UUID id);

    List<RendezVous> findByStatutAndDateHeureBetween(StatutRendezVous statut,
                                                     LocalDateTime startDateTime,
                                                     LocalDateTime endDateTime);
}
