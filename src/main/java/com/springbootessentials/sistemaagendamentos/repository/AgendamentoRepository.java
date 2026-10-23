package com.springbootessentials.sistemaagendamentos.repository;


import com.springbootessentials.sistemaagendamentos.domain.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Integer> {

}
