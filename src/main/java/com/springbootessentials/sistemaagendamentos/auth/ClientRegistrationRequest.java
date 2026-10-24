package com.springbootessentials.sistemaagendamentos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClientRegistrationRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(max = 20) String cpf,
        @NotBlank @Email @Size(max = 120) String email,
        @NotBlank @Size(min = 10, max = 72) String password
) {
}
