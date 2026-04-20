package com.clinicpro.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clinicpro.entity.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {

    List<Patient> findByNomContainingIgnoreCase(String nom);

    boolean existsByTel(String tel);

    boolean existsByTelAndIdNot(String tel, UUID id);
}
