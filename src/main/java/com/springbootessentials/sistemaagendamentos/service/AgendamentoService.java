package com.springbootessentials.sistemaagendamentos.service;

import com.springbootessentials.sistemaagendamentos.domain.Agendamento;
import com.springbootessentials.sistemaagendamentos.domain.Cliente;
import com.springbootessentials.sistemaagendamentos.domain.Doutor;
import com.springbootessentials.sistemaagendamentos.mapper.AgendamentoMapper;
import com.springbootessentials.sistemaagendamentos.repository.AgendamentoRepository;
import com.springbootessentials.sistemaagendamentos.repository.ClienteRepository;
import com.springbootessentials.sistemaagendamentos.repository.DoutorRepository;
import com.springbootessentials.sistemaagendamentos.requests.agendamentoRequests.AgendamentoPostRequest;
import com.springbootessentials.sistemaagendamentos.requests.agendamentoRequests.AgendamentoPutRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendamentoService {
    private final AgendamentoRepository agendamentoRepository;
    private final DoutorRepository doutorRepository;
    private final ClienteRepository clienteRepository;

    public List<Agendamento> listAll() {return agendamentoRepository.findAll(); }

    public Agendamento findById(Integer id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Agendamento nao encontrado"));
    }

    public Agendamento save(AgendamentoPostRequest agendamentoPostRequest) {
        Agendamento agendamento = AgendamentoMapper.INSTANCE.ToAgendamento(agendamentoPostRequest);
        setRelationships(agendamento, agendamentoPostRequest.getDoctorId(), agendamentoPostRequest.getClienteId());
        return agendamentoRepository.save(agendamento);
    }

    public Void delete(Integer id) {
        agendamentoRepository.delete(findById(id));
        return null;
    }

    public Void update(Integer id, AgendamentoPutRequest agendamentoPutRequest) {
        Agendamento savedAgendamento = findById(id);
        Agendamento agendamento = AgendamentoMapper.INSTANCE.ToAgendamento(agendamentoPutRequest);
        agendamento.setId(savedAgendamento.getId());
        setRelationships(agendamento, agendamentoPutRequest.getDoctorId(), agendamentoPutRequest.getClienteId());
        agendamentoRepository.save(agendamento);
        return null;
    }

    private void setRelationships(Agendamento agendamento, Integer doctorId, Integer clienteId) {
        Doutor doutor = doutorRepository.findById(doctorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doutor nao encontrado"));
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente nao encontrado"));
        agendamento.setDoutor(doutor);
        agendamento.setCliente(cliente);
    }


}
