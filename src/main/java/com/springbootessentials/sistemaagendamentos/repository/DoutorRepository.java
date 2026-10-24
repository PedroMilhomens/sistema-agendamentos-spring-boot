package com.springbootessentials.sistemaagendamentos.repository;

import com.springbootessentials.sistemaagendamentos.domain.Doutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;
import java.util.List;
import jakarta.persistence.LockModeType;


public interface DoutorRepository extends JpaRepository<Doutor, Integer> {
    List<Doutor> findAllByEmailIgnoreCase(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM Doutor d WHERE d.id = :id")
    Optional<Doutor> findByIdForUpdate(@Param("id") Integer id);

    @Query("SELECT d.id FROM Doutor d WHERE d.name = :name")
    Optional<Integer> findIdByName(@Param("name") String name);
}
