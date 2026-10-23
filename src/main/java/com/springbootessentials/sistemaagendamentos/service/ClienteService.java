package com.springbootessentials.sistemaagendamentos.service;

import com.springbootessentials.sistemaagendamentos.domain.Cliente;
import com.springbootessentials.sistemaagendamentos.mapper.ClienteMapper;
import com.springbootessentials.sistemaagendamentos.repository.ClienteRepository;
import com.springbootessentials.sistemaagendamentos.requests.clienteRequests.ClientePostRequest;
import com.springbootessentials.sistemaagendamentos.requests.clienteRequests.ClientePutRequest;
import com.springbootessentials.sistemaagendamentos.response.ClienteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    public List<ClienteResponse> findAll() {
        return ClienteMapper.INSTANCE.ToClienteResponses(clienteRepository.findAll());
    }

    public ClienteResponse findById(Integer id) {
        return ClienteMapper.INSTANCE.ToClienteResponse(findClienteById(id));
    }

    public Integer findIdByName(String name) {
        return clienteRepository.findIdByName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente nao encontrado"));
    }

    public void save(ClientePostRequest clientePostRequest) {
        Cliente cliente = ClienteMapper.INSTANCE.ToCliente(clientePostRequest);
        cliente.setPassword(passwordEncoder.encode(cliente.getPassword()));
        clienteRepository.save(cliente);
    }

    public Void delete(Integer id) {
        clienteRepository.delete(findClienteById(id));
        return null;
    }

    public Void update(Integer id, ClientePutRequest clientePutRequest) {
        Cliente savedCliente = findClienteById(id);
        Cliente cliente =  ClienteMapper.INSTANCE.ToCliente(clientePutRequest);
        cliente.setId(savedCliente.getId());
        if (clientePutRequest.getPassword() == null) {
            cliente.setPassword(savedCliente.getPassword());
        } else {
            cliente.setPassword(passwordEncoder.encode(clientePutRequest.getPassword()));
        }
        clienteRepository.save(cliente);
        return null;
    }

    private Cliente findClienteById(Integer id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente nao encontrado"));
    }

}
