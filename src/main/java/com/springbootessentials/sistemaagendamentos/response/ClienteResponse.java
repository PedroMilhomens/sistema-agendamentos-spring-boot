package com.springbootessentials.sistemaagendamentos.response;

import lombok.Data;

@Data
public class ClienteResponse {
    private Integer id;
    private String name;
    private String cpf;
    private String email;
}
