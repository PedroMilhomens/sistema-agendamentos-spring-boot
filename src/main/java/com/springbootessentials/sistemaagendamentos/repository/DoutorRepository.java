package com.springbootessentials.sistemaagendamentos.repository;

import com.springbootessentials.sistemaagendamentos.domain.Doutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface DoutorRepository extends JpaRepository<Doutor, Integer> {

    Optional<Doutor> findByName(String name);

    @Query("SELECT d.id FROM Doutor d WHERE d.name = :name")
    Optional<Integer> findIdByName(@Param("name") String name);
}
