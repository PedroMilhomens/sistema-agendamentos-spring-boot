package com.springbootessentials.sistemaagendamentos.controller;

import com.springbootessentials.sistemaagendamentos.domain.Agendamento;
import com.springbootessentials.sistemaagendamentos.requests.AgendamentoPostRequest;
import com.springbootessentials.sistemaagendamentos.requests.AgendamentoPutRequest;
import com.springbootessentials.sistemaagendamentos.service.AgendamentoService;
import com.springbootessentials.sistemaagendamentos.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/agendamentos")
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
        return ResponseEntity.ok(agendamentoService.findById(id));
    }

    @PostMapping(path = "/post")
    public ResponseEntity<Agendamento> save(@RequestBody AgendamentoPostRequest agendamentoPostRequest) {
        return ResponseEntity.ok(agendamentoService.save(agendamentoPostRequest));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        return new ResponseEntity<>(agendamentoService.delete(id),  HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/put")
    public ResponseEntity<Void> update(@RequestBody AgendamentoPutRequest agendamentoPutRequest) {
        return new ResponseEntity<>(agendamentoService.replace(agendamentoPutRequest), HttpStatus.NO_CONTENT);
    }
}
