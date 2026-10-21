package com.springbootessentials.sistemaagendamentos.controller;

import com.springbootessentials.sistemaagendamentos.domain.Doutor;
import com.springbootessentials.sistemaagendamentos.requests.doutorRequests.DoutorPostRequest;
import com.springbootessentials.sistemaagendamentos.requests.doutorRequests.DoutorPutRequest;
import com.springbootessentials.sistemaagendamentos.service.DoutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/doutores")
@Log4j2
@RequiredArgsConstructor
public class DoutorController {
    private final DoutorService doutorService;

    @GetMapping
    public List<Doutor> findAll() {
        return doutorService.findAll();
    }

    @GetMapping(path = "/by-id/{id}")
    public ResponseEntity<Doutor> findById(@PathVariable Integer id) {
        return new ResponseEntity<>(doutorService.findById(id), HttpStatus.OK);
    }

    @GetMapping(path = "/by-name/{name}")
    public ResponseEntity<Integer> findIdByName(@PathVariable String name) {
        return new ResponseEntity<>(doutorService.findIdByName(name), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Doutor> save(@RequestBody DoutorPostRequest doutorPostRequest) {
        return new ResponseEntity<>(doutorService.save(doutorPostRequest), HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/del-by-id/{id}")
    public ResponseEntity<Void> delete(@PathVariable @RequestBody Integer id) {
        return new ResponseEntity<>(doutorService.delete(id), HttpStatus.NO_CONTENT);
    }


    @PutMapping
    public ResponseEntity<Void> update(@RequestBody DoutorPutRequest doutorPutRequest) {
        return new ResponseEntity<>(doutorService.update(doutorPutRequest), HttpStatus.NO_CONTENT);
    }
}