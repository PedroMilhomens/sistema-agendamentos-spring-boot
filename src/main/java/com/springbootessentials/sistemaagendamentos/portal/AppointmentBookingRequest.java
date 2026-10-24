package com.springbootessentials.sistemaagendamentos.portal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentBookingRequest(
        @NotNull LocalDate data,
        @NotNull LocalTime hora,
        @NotBlank @Size(max = 255) String descricao,
        @NotNull Integer doctorId
) {
}
