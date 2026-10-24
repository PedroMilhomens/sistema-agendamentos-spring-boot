package com.springbootessentials.sistemaagendamentos.auth;

import com.springbootessentials.sistemaagendamentos.domain.Cliente;
import com.springbootessentials.sistemaagendamentos.domain.Doutor;
import com.springbootessentials.sistemaagendamentos.repository.ClienteRepository;
import com.springbootessentials.sistemaagendamentos.repository.DoutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PortalAuthService {
    private final ClienteRepository clienteRepository;
    private final DoutorRepository doutorRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.doctor-provisioning-token:}")
    private String doctorProvisioningToken;

    public PortalPrincipal authenticate(LoginRequest request) {
        String email = normalizeEmail(request.email());
        if (request.accountType() == AccountType.CLIENTE) {
            List<Cliente> matches = clienteRepository.findAllByEmailIgnoreCase(email);
            if (matches.size() == 1 && matches.get(0).getPassword() != null
                    && passwordEncoder.matches(request.password(), matches.get(0).getPassword())) {
                Cliente client = matches.get(0);
                return new PortalPrincipal(client.getId(), AccountType.CLIENTE, client.getName(), client.getEmail());
            }
        } else {
            List<Doutor> matches = doutorRepository.findAllByEmailIgnoreCase(email);
            if (matches.size() == 1 && matches.get(0).getPassword() != null
                    && passwordEncoder.matches(request.password(), matches.get(0).getPassword())) {
                Doutor doctor = matches.get(0);
                return new PortalPrincipal(doctor.getId(), AccountType.DOUTOR, doctor.getName(), doctor.getEmail());
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail, senha ou tipo de conta inválido.");
    }

    public PortalPrincipal registerClient(ClientRegistrationRequest request) {
        String email = normalizeEmail(request.email());
        if (!clienteRepository.findAllByEmailIgnoreCase(email).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma conta com este e-mail.");
        }
        Cliente client = Cliente.builder()
                .name(request.name().trim())
                .cpf(request.cpf().trim())
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .build();
        client = clienteRepository.save(client);
        return new PortalPrincipal(client.getId(), AccountType.CLIENTE, client.getName(), client.getEmail());
    }

    public PortalUserResponse currentUser(PortalPrincipal principal) {
        if (principal.accountType() == AccountType.CLIENTE) {
            Cliente client = clienteRepository.findById(principal.id())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Conta não encontrada."));
            return new PortalUserResponse(client.getId(), AccountType.CLIENTE.name(), client.getName(), client.getEmail());
        }
        Doutor doctor = doutorRepository.findById(principal.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Conta não encontrada."));
        return new PortalUserResponse(doctor.getId(), AccountType.DOUTOR.name(), doctor.getName(), doctor.getEmail());
    }

    public void provisionDoctor(DoctorProvisionRequest request, String suppliedToken) {
        if (doctorProvisioningToken == null || doctorProvisioningToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "O provisionamento de profissionais não está habilitado.");
        }
        if (suppliedToken == null || !MessageDigest.isEqual(
                doctorProvisioningToken.getBytes(StandardCharsets.UTF_8),
                suppliedToken.getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Credencial de provisionamento inválida.");
        }
        String email = normalizeEmail(request.email());
        if (!doutorRepository.findAllByEmailIgnoreCase(email).stream()
                .allMatch(doctor -> request.doctorId() != null && doctor.getId().equals(request.doctorId()))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este e-mail já está associado a outro profissional.");
        }
        Doutor doctor = request.doctorId() == null
                ? new Doutor()
                : doutorRepository.findById(request.doctorId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profissional não encontrado."));
        doctor.setName(request.name().trim());
        doctor.setAreaDeAtuacao(request.areaDeAtuacao().trim());
        doctor.setEmail(email);
        doctor.setPassword(passwordEncoder.encode(request.password()));
        doutorRepository.save(doctor);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(java.util.Locale.ROOT);
    }
}
