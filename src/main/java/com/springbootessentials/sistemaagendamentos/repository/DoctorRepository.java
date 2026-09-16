package com.springbootessentials.sistemaagendamentos.repository;

import com.springbootessentials.sistemaagendamentos.domain.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
}
