package com.springbootessentials.sistemaagendamentos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotNull AccountType accountType,
        @NotBlank @Email String email,
        @NotBlank String password
) {
}
