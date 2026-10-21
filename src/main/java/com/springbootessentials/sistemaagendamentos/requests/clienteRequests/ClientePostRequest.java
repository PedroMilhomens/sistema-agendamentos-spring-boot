package com.springbootessentials.sistemaagendamentos.requests.clienteRequests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClientePostRequest {
    private Integer id;
    @NotBlank
    private String name;
    @NotBlank
    private String cpf;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;
}
