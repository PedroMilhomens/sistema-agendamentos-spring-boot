package com.springbootessentials.sistemaagendamentos.portal;

import com.springbootessentials.sistemaagendamentos.domain.Cliente;

public record ClientProfileResponse(Integer id, String name, String cpf, String email) {
    public static ClientProfileResponse from(Cliente client) {
        return new ClientProfileResponse(client.getId(), client.getName(), client.getCpf(), client.getEmail());
    }
}
