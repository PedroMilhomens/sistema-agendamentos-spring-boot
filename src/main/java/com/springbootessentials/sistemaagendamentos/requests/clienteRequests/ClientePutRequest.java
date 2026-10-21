package com.springbootessentials.sistemaagendamentos.requests.clienteRequests;

import lombok.Data;

@Data
public class ClientePutRequest {
    private Integer id;
    private String name;
    private String cpf;
    private String email;
    private String password;
}
