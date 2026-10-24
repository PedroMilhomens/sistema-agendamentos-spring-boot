package com.springbootessentials.sistemaagendamentos.controller;

import com.springbootessentials.sistemaagendamentos.domain.Doutor;
import com.springbootessentials.sistemaagendamentos.portal.DoctorDirectoryResponse;
import com.springbootessentials.sistemaagendamentos.repository.AgendamentoRepository;
import com.springbootessentials.sistemaagendamentos.repository.DoutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;
import java.util.List;

@RestController
@RequestMapping("/api/doutores/directory")
@RequiredArgsConstructor
public class DoctorDirectoryController {
    private final DoutorRepository doutorRepository;
    private final AgendamentoRepository agendamentoRepository;

    @GetMapping
    public List<DoctorDirectoryResponse> search(@RequestParam(required = false) String especialidade,
                                                @RequestParam(required = false) LocalDate data,
                                                @RequestParam(required = false) LocalTime hora) {
        if ((data == null) != (hora == null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe data e horário para consultar disponibilidade.");
        }
        return doutorRepository.findAll().stream()
                .filter(doctor -> especialidade == null || especialidade.isBlank()
                        || (doctor.getAreaDeAtuacao() != null
                        && doctor.getAreaDeAtuacao().toLowerCase(Locale.ROOT).contains(especialidade.trim().toLowerCase(Locale.ROOT))))
                .map(doctor -> toResponse(doctor, data, hora))
                .toList();
    }

    private DoctorDirectoryResponse toResponse(Doutor doctor, LocalDate data, LocalTime hora) {
        Boolean available = data == null ? null
                : !agendamentoRepository.existsByDoutor_IdAndDataAndHora(doctor.getId(), data, hora);
        return new DoctorDirectoryResponse(doctor.getId(), doctor.getName(), doctor.getAreaDeAtuacao(), available);
    }
}
