package com.springbootessentials.sistemaagendamentos.service;

import com.springbootessentials.sistemaagendamentos.domain.Agendamento;
import com.springbootessentials.sistemaagendamentos.mapper.AgendamentoMapper;
import com.springbootessentials.sistemaagendamentos.repository.AgendamentoRepository;
import com.springbootessentials.sistemaagendamentos.requests.AgendamentoPostRequest;
import com.springbootessentials.sistemaagendamentos.requests.AgendamentoPutRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendamentoService {
    private final AgendamentoRepository agendamentoRepository;

    public List<Agendamento> listAll() {return agendamentoRepository.findAll(); }

    public Agendamento findById(Integer id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Agendamento nao encontrado"));
    }

    public Agendamento save(AgendamentoPostRequest agendamentoPostRequest) {
        return agendamentoRepository.save(AgendamentoMapper.INSTANCE.ToAgendamento(agendamentoPostRequest));
    }

    public Void delete(Integer id) {
        agendamentoRepository.deleteById(id);
        return null;
    }

    // UNFINISHED
    public Void replace(AgendamentoPutRequest agendamentoPutRequest) {
        Agendamento savedAgendamento = findById(agendamentoPutRequest.getId());
        Agendamento agendamento = AgendamentoMapper.INSTANCE.ToAgendamento(agendamentoPutRequest);
        agendamento.setId(savedAgendamento.getId());
        agendamentoRepository.save(agendamento);
        return null;
    }
}
