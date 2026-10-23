package com.springbootessentials.sistemaagendamentos.controller;

import com.springbootessentials.sistemaagendamentos.domain.Agendamento;
import com.springbootessentials.sistemaagendamentos.requests.agendamentoRequests.AgendamentoPostRequest;
import com.springbootessentials.sistemaagendamentos.requests.agendamentoRequests.AgendamentoPutRequest;
import com.springbootessentials.sistemaagendamentos.service.AgendamentoService;
import com.springbootessentials.sistemaagendamentos.util.DateUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/agendamentos")
@Log4j2
@RequiredArgsConstructor
public class AgendamentoController {
    private final DateUtil dateUtil;
    private final AgendamentoService agendamentoService;

    @GetMapping
    public List<Agendamento> listAll() {
        log.info(dateUtil.formatDateTimetoDatabaseStyle(LocalDateTime.now()));
        return agendamentoService.listAll();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Agendamento> findById(@PathVariable Integer id) {
        return new  ResponseEntity<>(agendamentoService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Agendamento> save(@Valid @RequestBody AgendamentoPostRequest agendamentoPostRequest) {
        return new ResponseEntity<>(agendamentoService.save(agendamentoPostRequest), HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        return new ResponseEntity<>(agendamentoService.delete(id),  HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Void> update(@PathVariable Integer id,
                                       @Valid @RequestBody AgendamentoPutRequest agendamentoPutRequest) {
        return new ResponseEntity<>(agendamentoService.update(id, agendamentoPutRequest), HttpStatus.NO_CONTENT);
    }
}
