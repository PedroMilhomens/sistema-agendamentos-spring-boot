package com.springbootessentials.sistemaagendamentos.service;

import com.springbootessentials.sistemaagendamentos.domain.Cliente;
import com.springbootessentials.sistemaagendamentos.mapper.ClienteMapper;
import com.springbootessentials.sistemaagendamentos.repository.ClienteRepository;
import com.springbootessentials.sistemaagendamentos.requests.clienteRequests.ClientePostRequest;
import com.springbootessentials.sistemaagendamentos.requests.clienteRequests.ClientePutRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {
    private final ClienteRepository clienteRepository;

    public List<Cliente> findAll() {return clienteRepository.findAll();}

    public Cliente findById(Integer id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente nao encontrado"));
    }

    public Integer findIdByName(String name) {
        return clienteRepository.findIdByName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente nao encontrado"));
    }

    public Cliente save(ClientePostRequest clientePostRequest) {
        return clienteRepository.save(ClienteMapper.INSTANCE.ToCliente(clientePostRequest));
    }

    public Void delete(Integer id) {
        clienteRepository.deleteById(id);
        return null;
    }

    public Void update(ClientePutRequest  clientePutRequest) {
        Cliente savedCliente = findById(clientePutRequest.getId());
        Cliente cliente =  ClienteMapper.INSTANCE.ToCliente(clientePutRequest);
        cliente.setId(savedCliente.getId());
        clienteRepository.save(cliente);
        return null;
    }

}
