package com.springbootessentials.sistemaagendamentos.service;

import com.springbootessentials.sistemaagendamentos.domain.Agendamento;
import com.springbootessentials.sistemaagendamentos.domain.Cliente;
import com.springbootessentials.sistemaagendamentos.domain.Doutor;
import com.springbootessentials.sistemaagendamentos.repository.AgendamentoRepository;
import com.springbootessentials.sistemaagendamentos.repository.ClienteRepository;
import com.springbootessentials.sistemaagendamentos.repository.DoutorRepository;
import com.springbootessentials.sistemaagendamentos.requests.agendamentoRequests.AgendamentoPostRequest;
import com.springbootessentials.sistemaagendamentos.requests.agendamentoRequests.AgendamentoPutRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AgendamentoServiceTest {
    private final AgendamentoRepository agendamentoRepository = mock(AgendamentoRepository.class);
    private final DoutorRepository doutorRepository = mock(DoutorRepository.class);
    private final ClienteRepository clienteRepository = mock(ClienteRepository.class);
    private final AgendamentoService service = new AgendamentoService(
            agendamentoRepository, doutorRepository, clienteRepository);

    @Test
    void savesAppointmentWithExistingDoctorAndClient() {
        Doutor doutor = Doutor.builder().id(3).name("Dr. Silva").build();
        Cliente cliente = Cliente.builder().id(8).name("Ana").build();
        when(doutorRepository.findById(3)).thenReturn(Optional.of(doutor));
        when(clienteRepository.findById(8)).thenReturn(Optional.of(cliente));
        when(agendamentoRepository.save(any(Agendamento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        AgendamentoPostRequest request = new AgendamentoPostRequest();
        request.setData(LocalDate.of(2026, 10, 21));
        request.setHora(LocalTime.of(9, 30));
        request.setDescricao("Consulta");
        request.setDoctorId(3);
        request.setClienteId(8);

        Agendamento saved = service.save(request);

        assertSame(doutor, saved.getDoutor());
        assertSame(cliente, saved.getCliente());
        assertEquals(LocalDate.of(2026, 10, 21), saved.getData());
        assertEquals(LocalTime.of(9, 30), saved.getHora());
    }

    @Test
    void rejectsAppointmentWhenDoctorDoesNotExist() {
        when(doutorRepository.findById(3)).thenReturn(Optional.empty());
        AgendamentoPostRequest request = new AgendamentoPostRequest();
        request.setDoctorId(3);
        request.setClienteId(8);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> service.save(request));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(agendamentoRepository, never()).save(any(Agendamento.class));
    }

    @Test
    void replacesAppointmentAndKeepsItsId() {
        Doutor doutor = Doutor.builder().id(3).build();
        Cliente cliente = Cliente.builder().id(8).build();
        Agendamento existing = Agendamento.builder().id(12).build();
        when(agendamentoRepository.findById(12)).thenReturn(Optional.of(existing));
        when(doutorRepository.findById(3)).thenReturn(Optional.of(doutor));
        when(clienteRepository.findById(8)).thenReturn(Optional.of(cliente));
        AgendamentoPutRequest request = new AgendamentoPutRequest();
        request.setData(LocalDate.of(2026, 10, 22));
        request.setHora(LocalTime.of(10, 0));
        request.setDescricao("Retorno");
        request.setDoctorId(3);
        request.setClienteId(8);

        service.update(12, request);

        var saved = org.mockito.ArgumentCaptor.forClass(Agendamento.class);
        verify(agendamentoRepository).save(saved.capture());
        assertEquals(12, saved.getValue().getId());
        assertSame(doutor, saved.getValue().getDoutor());
        assertSame(cliente, saved.getValue().getCliente());
    }

    @Test
    void returnsNotFoundWhenDeletingMissingAppointment() {
        when(agendamentoRepository.findById(12)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> service.delete(12));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
}
