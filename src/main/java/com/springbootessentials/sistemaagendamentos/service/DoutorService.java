package com.springbootessentials.sistemaagendamentos.service;

import com.springbootessentials.sistemaagendamentos.domain.Doutor;
import com.springbootessentials.sistemaagendamentos.mapper.DoutorMapper;
import com.springbootessentials.sistemaagendamentos.repository.DoutorRepository;
import com.springbootessentials.sistemaagendamentos.requests.doutorRequests.DoutorPostRequest;
import com.springbootessentials.sistemaagendamentos.requests.doutorRequests.DoutorPutRequest;
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

    public Doutor save(DoutorPostRequest doutorPostRequest) {
        return doutorRepository.save(DoutorMapper.INSTANCE.ToDoutor(doutorPostRequest));
    }

    public Void delete(Integer id) {
        doutorRepository.deleteById(id);
        return null;
    }

    public Void update(DoutorPutRequest doutorPutRequest) {
        Doutor savedDoutor = findById(doutorPutRequest.getId());
        Doutor doutor = DoutorMapper.INSTANCE.ToDoutor(doutorPutRequest);
        doutor.setId(savedDoutor.getId());
        doutorRepository.save(doutor);
        return null;
    }
}
