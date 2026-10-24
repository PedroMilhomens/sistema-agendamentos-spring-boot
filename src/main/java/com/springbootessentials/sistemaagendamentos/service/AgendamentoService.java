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
import com.springbootessentials.sistemaagendamentos.portal.AppointmentBookingRequest;
import com.springbootessentials.sistemaagendamentos.portal.AppointmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AgendamentoService {
    private final AgendamentoRepository agendamentoRepository;
    private final DoutorRepository doutorRepository;
    private final ClienteRepository clienteRepository;

    public List<Agendamento> listAll() {return agendamentoRepository.findAll(); }

    public List<AppointmentResponse> listForClient(Integer clientId) {
        return agendamentoRepository.findByCliente_IdOrderByDataAscHoraAsc(clientId)
                .stream().map(AppointmentResponse::from).toList();
    }

    public List<AppointmentResponse> listForDoctor(Integer doctorId) {
        return agendamentoRepository.findByDoutor_IdOrderByDataAscHoraAsc(doctorId)
                .stream().map(AppointmentResponse::from).toList();
    }

    @Transactional
    public AppointmentResponse bookForClient(Integer clientId, AppointmentBookingRequest request) {
        if (LocalDateTime.of(request.data(), request.hora()).isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Escolha uma data e horário futuros.");
        }
        Doutor doutor = doutorRepository.findByIdForUpdate(request.doctorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profissional não encontrado."));
        if (agendamentoRepository.existsByDoutor_IdAndDataAndHora(request.doctorId(), request.data(), request.hora())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este horário acabou de ser reservado. Escolha outro.");
        }
        Cliente cliente = clienteRepository.findById(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Conta de cliente não encontrada."));
        Agendamento appointment = Agendamento.builder()
                .data(request.data())
                .hora(request.hora())
                .descricao(request.descricao().trim())
                .doutor(doutor)
                .cliente(cliente)
                .build();
        return AppointmentResponse.from(agendamentoRepository.save(appointment));
    }

    public void cancelForClient(Integer clientId, Integer appointmentId) {
        Agendamento appointment = findById(appointmentId);
        if (!appointment.getCliente().getId().equals(clientId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Agendamento não encontrado.");
        }
        if (LocalDateTime.of(appointment.getData(), appointment.getHora()).isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é possível cancelar um atendimento passado.");
        }
        agendamentoRepository.delete(appointment);
    }

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
