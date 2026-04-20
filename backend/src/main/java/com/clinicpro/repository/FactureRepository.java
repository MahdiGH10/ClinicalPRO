package com.clinicpro.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clinicpro.entity.Facture;
import com.clinicpro.entity.StatutPaiement;

@Repository
public interface FactureRepository extends JpaRepository<Facture, UUID> {

    Optional<Facture> findByConsultation_Id(UUID consultationId);

    List<Facture> findByStatutPaiement(StatutPaiement statutPaiement);

    @Query("select count(f) from Facture f where year(f.dateEmission) = :year and month(f.dateEmission) = :month")
    long countByYearAndMonth(@Param("year") int year, @Param("month") int month);
}
