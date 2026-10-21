package com.springbootessentials.sistemaagendamentos.service;

import com.springbootessentials.sistemaagendamentos.domain.Cliente;
import com.springbootessentials.sistemaagendamentos.repository.ClienteRepository;
import com.springbootessentials.sistemaagendamentos.requests.clienteRequests.ClientePostRequest;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.when;

class ClienteServiceTest {
    @Test
    void savesEncodedPassword() {
        ClienteRepository repository = mock(ClienteRepository.class);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        ClienteService service = new ClienteService(repository, passwordEncoder);
        when(repository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ClientePostRequest request = new ClientePostRequest();
        request.setName("Ana");
        request.setCpf("12345678900");
        request.setEmail("ana@example.com");
        request.setPassword("secret");

        service.save(request);
        ArgumentCaptor<Cliente> savedCliente = ArgumentCaptor.forClass(Cliente.class);
        verify(repository).save(savedCliente.capture());

        assertTrue(passwordEncoder.matches("secret", savedCliente.getValue().getPassword()));
    }
}
