package com.springbootessentials.sistemaagendamentos.controller;

import com.springbootessentials.sistemaagendamentos.domain.Cliente;
import com.springbootessentials.sistemaagendamentos.requests.clienteRequests.ClientePostRequest;
import com.springbootessentials.sistemaagendamentos.requests.clienteRequests.ClientePutRequest;
import com.springbootessentials.sistemaagendamentos.service.ClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/clientes")
@Log4j2
@RequiredArgsConstructor
public class ClienteController {
    private final ClienteService clienteService;

    @GetMapping
    public List<Cliente> findAll() {
        return clienteService.findAll();
    }

    @GetMapping(path = "/by-id/{id}")
    public ResponseEntity<Cliente> findById(@PathVariable Integer id) {
        return new ResponseEntity<>(clienteService.findById(id), HttpStatus.OK);
    }

    @GetMapping(path = "/by-name/{name}")
    public ResponseEntity<Integer> findIdByName(@PathVariable String name) {
        return new ResponseEntity<>(clienteService.findIdByName(name), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Cliente> save(@RequestBody ClientePostRequest clientePostRequest) {
        return new ResponseEntity<>(clienteService.save(clientePostRequest), HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/del-by-id/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        return new ResponseEntity<>(clienteService.delete(id), HttpStatus.NO_CONTENT);
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody ClientePutRequest clientePutRequest) {
        return new ResponseEntity<>(clienteService.update(clientePutRequest), HttpStatus.NO_CONTENT);
    }

}
