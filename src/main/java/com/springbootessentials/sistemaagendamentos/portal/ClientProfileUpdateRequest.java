package com.springbootessentials.sistemaagendamentos.portal;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClientProfileUpdateRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(max = 20) String cpf,
        @NotBlank @Email @Size(max = 120) String email,
        @NotBlank String currentPassword,
        @Size(min = 10, max = 72) String newPassword
) {
}
