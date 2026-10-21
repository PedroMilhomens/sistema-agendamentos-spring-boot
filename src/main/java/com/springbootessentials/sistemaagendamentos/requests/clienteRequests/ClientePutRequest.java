package com.springbootessentials.sistemaagendamentos.requests.clienteRequests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ClientePutRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String cpf;
    @NotBlank
    @Email
    private String email;
    @Pattern(regexp = ".*\\S.*", message = "Password must not be blank")
    private String password;
}
