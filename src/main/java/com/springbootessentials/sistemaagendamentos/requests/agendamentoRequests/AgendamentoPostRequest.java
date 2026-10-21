package com.springbootessentials.sistemaagendamentos.requests.agendamentoRequests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AgendamentoPostRequest {
    @NotNull
    private LocalDate data;
    @NotNull
    private LocalTime hora;
    @NotBlank
    private String descricao;
    @NotNull
    private Integer doctorId;
    @NotNull
    private Integer clienteId;
}
