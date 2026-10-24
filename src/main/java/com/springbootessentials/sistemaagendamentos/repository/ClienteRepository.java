package com.springbootessentials.sistemaagendamentos.repository;

import com.springbootessentials.sistemaagendamentos.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    List<Cliente> findAllByEmailIgnoreCase(String email);

    @Query("SELECT c.id FROM Cliente c WHERE c.name = :name")
    Optional<Integer> findIdByName(@Param("name") String name);
}
