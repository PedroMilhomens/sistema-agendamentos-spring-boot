package com.springbootessentials.sistemaagendamentos.controller;

import com.springbootessentials.sistemaagendamentos.requests.clienteRequests.ClientePostRequest;
import com.springbootessentials.sistemaagendamentos.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ClienteControllerTest {
    @Test
    void createReturnsCreatedWithoutBody() {
        ClienteService clienteService = mock(ClienteService.class);
        ClienteController controller = new ClienteController(clienteService);

        ResponseEntity<Void> response = controller.save(new ClientePostRequest());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNull(response.getBody());
        verify(clienteService).save(any(ClientePostRequest.class));
    }
}
