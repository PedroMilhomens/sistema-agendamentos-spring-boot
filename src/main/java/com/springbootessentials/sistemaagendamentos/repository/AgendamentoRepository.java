package com.springbootessentials.sistemaagendamentos.repository;


import com.springbootessentials.sistemaagendamentos.domain.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Integer> {
    List<Agendamento> findByCliente_IdOrderByDataAscHoraAsc(Integer clienteId);

    List<Agendamento> findByDoutor_IdOrderByDataAscHoraAsc(Integer doutorId);

    boolean existsByDoutor_IdAndDataAndHora(Integer doutorId, LocalDate data, LocalTime hora);
}
