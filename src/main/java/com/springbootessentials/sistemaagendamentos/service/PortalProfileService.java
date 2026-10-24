package com.springbootessentials.sistemaagendamentos.service;

import com.springbootessentials.sistemaagendamentos.auth.PortalPrincipal;
import com.springbootessentials.sistemaagendamentos.domain.Cliente;
import com.springbootessentials.sistemaagendamentos.domain.Doutor;
import com.springbootessentials.sistemaagendamentos.portal.ClientProfileResponse;
import com.springbootessentials.sistemaagendamentos.portal.ClientProfileUpdateRequest;
import com.springbootessentials.sistemaagendamentos.portal.DoctorProfileResponse;
import com.springbootessentials.sistemaagendamentos.portal.DoctorProfileUpdateRequest;
import com.springbootessentials.sistemaagendamentos.repository.ClienteRepository;
import com.springbootessentials.sistemaagendamentos.repository.DoutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PortalProfileService {
    private final ClienteRepository clienteRepository;
    private final DoutorRepository doutorRepository;
    private final PasswordEncoder passwordEncoder;

    public ClientProfileResponse getClientProfile(Integer clientId) {
        return ClientProfileResponse.from(findClient(clientId));
    }

    public DoctorProfileResponse getDoctorProfile(Integer doctorId) {
        return DoctorProfileResponse.from(findDoctor(doctorId));
    }

    public ClientProfileResponse updateClientProfile(Integer clientId, ClientProfileUpdateRequest request) {
        Cliente client = findClient(clientId);
        verifyCurrentPassword(request.currentPassword(), client.getPassword());
        String email = normalizeEmail(request.email());
        boolean emailInUse = clienteRepository.findAllByEmailIgnoreCase(email).stream()
                .anyMatch(existing -> !existing.getId().equals(clientId));
        if (emailInUse) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma conta com este e-mail.");
        }
        client.setName(request.name().trim());
        client.setCpf(request.cpf().trim());
        client.setEmail(email);
        if (request.newPassword() != null && !request.newPassword().isBlank()) {
            client.setPassword(passwordEncoder.encode(request.newPassword()));
        }
        return ClientProfileResponse.from(clienteRepository.save(client));
    }

    public DoctorProfileResponse updateDoctorProfile(Integer doctorId, DoctorProfileUpdateRequest request) {
        Doutor doctor = findDoctor(doctorId);
        verifyCurrentPassword(request.currentPassword(), doctor.getPassword());
        String email = normalizeEmail(request.email());
        boolean emailInUse = doutorRepository.findAllByEmailIgnoreCase(email).stream()
                .anyMatch(existing -> !existing.getId().equals(doctorId));
        if (emailInUse) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este e-mail já pertence a outro profissional.");
        }
        doctor.setName(request.name().trim());
        doctor.setAreaDeAtuacao(request.areaDeAtuacao().trim());
        doctor.setEmail(email);
        if (request.newPassword() != null && !request.newPassword().isBlank()) {
            doctor.setPassword(passwordEncoder.encode(request.newPassword()));
        }
        return DoctorProfileResponse.from(doutorRepository.save(doctor));
    }

    private void verifyCurrentPassword(String currentPassword, String encodedPassword) {
        if (encodedPassword == null || !passwordEncoder.matches(currentPassword, encodedPassword)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A senha atual está incorreta.");
        }
    }

    private Cliente findClient(Integer id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado."));
    }

    private Doutor findDoctor(Integer id) {
        return doutorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profissional não encontrado."));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
