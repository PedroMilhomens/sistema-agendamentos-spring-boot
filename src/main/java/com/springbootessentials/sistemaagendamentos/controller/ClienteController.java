package com.springbootessentials.sistemaagendamentos.controller;

import com.springbootessentials.sistemaagendamentos.requests.clienteRequests.ClientePostRequest;
import com.springbootessentials.sistemaagendamentos.requests.clienteRequests.ClientePutRequest;
import com.springbootessentials.sistemaagendamentos.response.ClienteResponse;
import com.springbootessentials.sistemaagendamentos.service.ClienteService;
import jakarta.validation.Valid;
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
    public List<ClienteResponse> findAll() {
        return clienteService.findAll();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ClienteResponse> findById(@PathVariable Integer id) {
        return new ResponseEntity<>(clienteService.findById(id), HttpStatus.OK);
    }

    @GetMapping(path = "/by-name/{name}")
    public ResponseEntity<Integer> findIdByName(@PathVariable String name) {
        return new ResponseEntity<>(clienteService.findIdByName(name), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> save(@Valid @RequestBody ClientePostRequest clientePostRequest) {
        clienteService.save(clientePostRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        return new ResponseEntity<>(clienteService.delete(id), HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Void> update(@PathVariable Integer id, @Valid @RequestBody ClientePutRequest clientePutRequest) {
        return new ResponseEntity<>(clienteService.update(id, clientePutRequest), HttpStatus.NO_CONTENT);
    }

}
