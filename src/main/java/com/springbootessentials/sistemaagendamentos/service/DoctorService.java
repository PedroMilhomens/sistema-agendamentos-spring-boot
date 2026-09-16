package com.springbootessentials.sistemaagendamentos.service;

import com.springbootessentials.sistemaagendamentos.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;

}
