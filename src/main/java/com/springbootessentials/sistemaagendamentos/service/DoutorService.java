package com.springbootessentials.sistemaagendamentos.service;

import com.springbootessentials.sistemaagendamentos.domain.Doutor;
import com.springbootessentials.sistemaagendamentos.repository.DoutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoutorService {
    private final DoutorRepository doutorRepository;

    public List<Doutor> findAll() {return doutorRepository.findAll();}

    public Doutor findById(Integer id) {
        return doutorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doutor nao encontrado"));
    }

    public Integer findIdByName(String name) {
        return doutorRepository.findIdByName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doutor nao encontrado"));
    }

    public Doutor save(Doutor doutor) {
        return doutorRepository.save(doutor);
    }
}
