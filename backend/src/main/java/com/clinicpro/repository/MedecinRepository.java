package com.clinicpro.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clinicpro.entity.Medecin;

@Repository
public interface MedecinRepository extends JpaRepository<Medecin, UUID> {

    List<Medecin> findByNomContainingIgnoreCase(String nom);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);

    boolean existsByTel(String tel);

    boolean existsByTelAndIdNot(String tel, UUID id);
}